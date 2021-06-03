package ink.echol.outofplacecraft.entities.yinglet.rendering;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class YingletClientEventHandler {

	@SubscribeEvent
	public static void thirdPersonPreRender(RenderPlayerEvent.Pre renderPlayerEvent) {
		// TODO test for being a yinglet
		if(true) {
			//renderPlayerEvent.setCanceled(true);
			// TODO render the yinglet

		}
	}
}
