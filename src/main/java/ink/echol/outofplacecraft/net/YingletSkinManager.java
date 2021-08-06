package ink.echol.outofplacecraft.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class YingletSkinManager {
    private static Logger logger = LogManager.getLogger(OutOfPlacecraftMod.MODID);

    private static final String SKIN_SYSTEM_FOLDER = "yinglet_skin_system/";
    private static final String SKIN_FOLDER = SKIN_SYSTEM_FOLDER + "skins/";
    private static HashMap<UUID, Future<IoUtils.FileRequest>> filesPending = new HashMap<>();

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

    public static void queueDownloadSkin(String skinUrl, UUID playerId) {
        boolean test = Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER;

        if(test) {
            logger.log(Level.INFO, "Downloading skin on SERVER" );
        }
        else {
            logger.log(Level.INFO, "Downloading skin on CLIENT" );
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

    public static void writeIndex() {
        JsonArray entries = new JsonArray();
        skinIndex.forEach( (UUID id, SkinEntry dat) -> {
            JsonObject entry = new JsonObject();
            entry.addProperty("Player", id.toString());
            entry.addProperty("File", dat.file);
            entry.addProperty("Url", dat.url);
            entry.addProperty("Hash", dat.hash);

            entries.add(entry);
        });
        try {
            OpenOption[] options = new OpenOption[] { StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE };
            Files.write(Paths.get(SKIN_SYSTEM_FOLDER + "index.json"), entries.toString().getBytes(StandardCharsets.UTF_8), options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}