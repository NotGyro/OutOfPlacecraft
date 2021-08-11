package ink.echol.outofplacecraft.client;

import com.mojang.blaze3d.systems.RenderSystem;
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

    public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(OutOfPlacecraftMod.MODID, "textures/yinglet/default.png");

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
            // ONLY DO THIS if we're actually in a game.
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> {
                    Minecraft.getInstance().textureManager.release(loadedCurrent.get(playerId).location);
                    loadedCurrent.remove(playerId);
                    setupTextureFor(playerId);
                });
            } else {
                Minecraft.getInstance().textureManager.release(loadedCurrent.get(playerId).location);
                loadedCurrent.remove(playerId);
                setupTextureFor(playerId);
            }
        }
        else {
            ensureLoaded(playerId);
        }
    }

    public static InputStream loadTextureFromFile(Path pth) throws IOException {
        InputStream io = Files.newInputStream(pth);
        return io;
    }

    public static ResourceLocation setupTextureFor(UUID id) {
        if( Minecraft.getInstance() != null) {
            if (Minecraft.getInstance().textureManager != null){
                if (YingletSkinManager.getClient().skinIndex.containsKey(id)) {
                    YingletSkinManager.SkinEntry entry = YingletSkinManager.getClient().skinIndex.get(id);
                    ResourceLocation resourceId = new ResourceLocation(SKIN_RESOURCE_PACK, id.toString().toLowerCase());
                    try {
                        Path path = Paths.get(YingletSkinManager.SKIN_FOLDER + entry.file);
                        if (!Files.exists(path)) {
                            //Just in case it's registered but not downloaded yet.
                            YingletSkinManager.getClient().queueDownloadSkin(entry.url, id);
                            return DEFAULT_TEXTURE;
                        }
                        // Load our file
                        InputStream imageData = loadTextureFromFile(path);
                        NativeImage image = NativeImage.read(NativeImage.PixelFormat.RGBA, imageData);
                        // Create an image from it
                        DynamicTexture tex = new DynamicTexture(image);
                        tex.upload();

                        // Give it to our texture manager.
                        // This is a method overload that does something clever with dynamic textures!
                        ResourceLocation loc = Minecraft.getInstance().textureManager.register("ying/" + id.toString(), tex);

                        loadedCurrent.put(id, new LoadedTextureEntry(loc, tex));

                    } catch (IOException e) {
                        e.printStackTrace();
                        //Make sure it doesn't spam this message perpetually, by removing the old skin.
                        YingletSkinManager.getClient().deleteSkin(id);
                    }
                }
            }
        }
        return DEFAULT_TEXTURE;
    }

    public static ResourceLocation ensureLoaded(UUID id) {
        if(YingletSkinManager.getClient().skinIndex.containsKey(id)) {
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
            return DEFAULT_TEXTURE;
        }
    }
}
