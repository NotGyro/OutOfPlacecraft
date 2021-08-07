package ink.echol.outofplacecraft.client;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.net.YingletSkinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class SkinTextureLoader {
    public static final String SKIN_RESOURCE_PACK = OutOfPlacecraftMod.MODID;

    public static class LoadedTextureEntry {
        public final ResourceLocation location;
        public final DynamicTexture texture;

        public LoadedTextureEntry(ResourceLocation location, DynamicTexture texture) {
            this.location = location;
            this.texture = texture;
        }
    }

    public static HashMap<UUID, LoadedTextureEntry> loadedCurrent = new HashMap<>();


    // Reload the texture for a playerr if the player's skin changes
    public static void reloadTexture(UUID playerId) {
        if( loadedCurrent.containsKey(playerId)  ) {

            Minecraft.getInstance().textureManager.release(loadedCurrent.get(playerId).location);
            loadedCurrent.remove(playerId);
            setupTextureFor(playerId);
        }
    }

    public static InputStream loadTextureFromFile(Path pth) throws IOException {
        InputStream io = Files.newInputStream(pth);
        return io;
    }

    public static ResourceLocation setupTextureFor(UUID id) {
        if(YingletSkinManager.skinIndex.containsKey(id)) {
            YingletSkinManager.SkinEntry entry = YingletSkinManager.skinIndex.get(id);
            ResourceLocation resourceId = new ResourceLocation(SKIN_RESOURCE_PACK, id.toString().toLowerCase());
            try {
                // Load our file
                InputStream imageData = loadTextureFromFile(Paths.get(YingletSkinManager.SKIN_FOLDER + entry.file));
                NativeImage image = NativeImage.read(NativeImage.PixelFormat.RGBA, imageData);
                // Create an image from it
                DynamicTexture tex = new DynamicTexture(image);
                tex.upload();

                // Give it to our texture manager.
                // This is a method overload that does something clever with dynamic textures!
                ResourceLocation loc = Minecraft.getInstance().textureManager.register("ying/"+id.toString(), tex);

                loadedCurrent.put(id, new LoadedTextureEntry(loc, tex));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResourceLocation(OutOfPlacecraftMod.MODID, "textures/yinglet/default.png");
    }

    public static ResourceLocation ensureLoaded(UUID id) {
        if(YingletSkinManager.skinIndex.containsKey(id)) {
            // There SHOULD be a texture for this user (per skinIndex containing a key for this uuid), but there isn't yet:
            // Therefore, we need to load one.
            if( !loadedCurrent.containsKey(id)) {
                return setupTextureFor(id);
            }
            else {
                return loadedCurrent.get(id).location;
            }
        }
        else {
            return new ResourceLocation(OutOfPlacecraftMod.MODID, "textures/yinglet/default.png");
        }
    }
}