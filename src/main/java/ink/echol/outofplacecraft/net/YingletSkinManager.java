package ink.echol.outofplacecraft.net;

import com.google.gson.*;
import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.capabilities.SpeciesCapability;
import ink.echol.outofplacecraft.client.SkinTextureLoader;
import javafx.scene.control.Skin;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(modid = OutOfPlacecraftMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class YingletSkinManager {
    private static Logger logger = LogManager.getLogger(OutOfPlacecraftMod.MODID);

    public static final String SKIN_SYSTEM_FOLDER = "yinglet_skin_system/";
    public static final String SKIN_FOLDER = SKIN_SYSTEM_FOLDER + "skins/";
    private static HashMap<UUID, Future<IoUtils.FileRequest>> filesPending = new HashMap<>();

    // Called when the server receives a message from a client saying "I have a new skin!"
    public static void syncSkinServerSide(UUID playerId, String skinURL) {
        if(skinIndex.containsKey(playerId)) {
            if(skinIndex.get(playerId).url.equalsIgnoreCase(skinURL)){
                //Syncing state we already know of, no need to update anything.
                return;
            }
        }
        // Server needs to download it to have a valid hash.
        queueDownloadSkin(skinURL, playerId);
    }

    public static class SkinEntry {
        public String file;
        public String url;
        public String hash;

        public SkinEntry(String f, String u, String h) {
            file = f;
            url = u;
            hash = h;
        }
    }

    public static HashMap<UUID, SkinEntry> skinIndex = new HashMap<>();

    public static HashMap<UUID, SkinEntry> loadIndexFromJson(String deserialized) {
        HashMap<UUID, SkinEntry> result = new HashMap<>();

        if(deserialized.length() > 8) {
            System.out.println("It thinks the file is " + deserialized);
            JsonArray jsonArray = (JsonArray) (new JsonParser().parse(deserialized));
            Iterator<JsonElement> iter = jsonArray.iterator();
            while(iter.hasNext()) {
                JsonElement elem = iter.next();
                if(elem.isJsonObject()) {
                    JsonObject obj = elem.getAsJsonObject();
                    UUID uuid = UUID.fromString(obj.get("Player").getAsString());
                    String file = obj.get("File").getAsString();
                    String url = obj.get("Url").getAsString();
                    String hash = obj.get("Hash").getAsString();

                    SkinEntry entry = new SkinEntry(file, url, hash);
                    result.put(uuid, entry);
                }
            }
        }
        return result;
    }

    //Returns a flag telling you if anything on the index has actually changed.
    public static boolean mergeIndexFrom(HashMap<UUID, SkinEntry> foreignIndex) {
        boolean anythingChanged = false;
        Iterator<UUID> keySet = foreignIndex.keySet().iterator();
        while(keySet.hasNext()) {
            UUID key = keySet.next();
            if( skinIndex.containsKey(key) ) {
                SkinEntry ourEntry = skinIndex.get(key);
                if(!ourEntry.hash.equalsIgnoreCase(foreignIndex.get(key).hash)) {
                    skinIndex.put(key, foreignIndex.get(key));
                    anythingChanged = true;
                }
            }
            else {
                skinIndex.put(key, foreignIndex.get(key));
                anythingChanged = true;
            }
        }
        return anythingChanged;
    }

    public static void loadIndexOnLaunch() {
        try {
            String filePath = SKIN_SYSTEM_FOLDER + "index.json";
            System.out.println("Loading from " + filePath);
            List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            String idxFile = "";
            lines.forEach( (str) -> {
                idxFile.concat(str);
            });
            skinIndex = loadIndexFromJson(idxFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //We only actually need to do this clientside.
        if( Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT ) {
            validateOrDownloadFromIndex();
        }
    }

    public static void clientSyncIncomingSkin(UUID playerId, String url, String hash) {
        SkinEntry result = null;
        Iterator<UUID> keySet = skinIndex.keySet().iterator();
        logger.log(Level.INFO, "Received information on yinglet skin for \"" + playerId + "\" with url \"" + url);
        while(keySet.hasNext()) {
            UUID key = keySet.next();
            SkinEntry entry = skinIndex.get(key);
            if(entry.hash.equalsIgnoreCase(hash)){
                logger.log(Level.INFO, "Yinglet skin with url \"" + url + "\" matches existing hash! Using old file.");
                result = entry;
            }
        }
        //If this file didn't already exist
        if(result == null) {
            logger.log(Level.INFO, "File from url \"" + url + "\" is new to us! Downloading a copy.");
            queueDownloadSkin(url, playerId);
        }
        //Identical skin is already loaded, just reuse it.
        else {
            skinIndex.put(playerId, result);
            SkinTextureLoader.reloadTexture(playerId);
        }
    }

    public static void queueDownloadSkin(String skinUrl, UUID playerId) {
        boolean test = Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER;

        if(test) {
            logger.log(Level.INFO, "Downloading skin on the SERVER thread group" );
        }
        else {
            logger.log(Level.INFO, "Downloading skin on the CLIENT thread group" );
        }

        //We're already working on this one, don't worry about it.
        if(filesPending.containsKey(playerId)) {
            return;
        }

        String[] urlSplit = skinUrl.split("/");
        String fileNameIn = urlSplit[urlSplit.length-1];

        String[] fixSplit = fileNameIn.split("\\?");
        fileNameIn = fixSplit[0];

        logger.log(Level.INFO, "Downloading skin from \"" + skinUrl + "\" to file \"" + fileNameIn );
        IoUtils.FileRequest req = new IoUtils.FileRequest(playerId.toString(), SKIN_FOLDER, fileNameIn, skinUrl);

        List<IoUtils.PendingRequest> dl = IoUtils.download( Collections.singletonList(req) );

        dl.stream().forEach( (IoUtils.PendingRequest p) -> {
            UUID playerUUID = UUID.fromString(p.getIdent());
            filesPending.put(playerUUID, p.getRequest());
        });
    }

    public static void validateOrDownloadFromIndex() {
        Iterator<UUID> keySet = skinIndex.keySet().iterator();
        while(keySet.hasNext()) {
            UUID key = keySet.next();
            SkinEntry entry = skinIndex.get(key);
            String filepath = SKIN_FOLDER + entry.file;
            if( Files.exists(Paths.get(filepath)) ) {
                String hash = retrieveHash(filepath);
                //We have the file, but the hash doesn't match! Redownload it.
                if( !hash.equalsIgnoreCase(entry.hash)) {
                    queueDownloadSkin(entry.url, key);
                }

            }
            else {
                //File's not there. Download.
                queueDownloadSkin(entry.url, key);
            }
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent evt) {
        pollRequests();
    }

    public static void pollRequests() {
        ArrayList<UUID> toRemove = new ArrayList<>();
        filesPending.forEach( (UUID id, Future<IoUtils.FileRequest> pend) -> {
            if( pend.isDone() ) {
                try {
                    IoUtils.FileRequest req = pend.get();
                    logger.log(Level.INFO, "Completed downloading a skin from \"" + req.getUrl() + "\" to \"" + req.getFileName());

                    String file = req.getFileName();

                    String hash = retrieveHash(SKIN_FOLDER + file);
                    skinIndex.put(id, new SkinEntry(file, req.getUrl(), hash) );

                    toRemove.add(id);

                    writeIndex();

                    //Should we multicast this information out to everyone?
                    if(Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
                        //Serversided! Let our clients know about the file.
                        SyncSkinPkt pkt = new SyncSkinPkt(id, req.getUrl(), hash);
                        OOPCPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), pkt);
                    } else {
                        //We are on client
                        //make sure it updates if changed.
                        SkinTextureLoader.reloadTexture(id);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        toRemove.forEach( (UUID id) -> {
            filesPending.remove(id);
        });
    }

    private static String retrieveHash(String filepath) {
        File file = new File(filepath);
        String result = new String();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            result = checksum(digest, file);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String checksum(MessageDigest digest, File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        fis.close();
        byte[] bytes = digest.digest();

        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String stringifyIndex() {
        JsonArray entries = new JsonArray();
        skinIndex.forEach( (UUID id, SkinEntry dat) -> {
            JsonObject entry = new JsonObject();
            entry.addProperty("Player", id.toString());
            entry.addProperty("File", dat.file);
            entry.addProperty("Url", dat.url);
            entry.addProperty("Hash", dat.hash);

            entries.add(entry);
        });
        return entries.toString();
    }

    public static void writeIndex() {
        String entires = stringifyIndex();
        try {
            OpenOption[] options = new OpenOption[] { StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE };
            Files.write(Paths.get(SKIN_SYSTEM_FOLDER + "index.json"), entires.getBytes(StandardCharsets.UTF_8), options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}