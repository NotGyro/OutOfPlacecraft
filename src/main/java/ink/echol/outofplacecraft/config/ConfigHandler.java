package ink.echol.outofplacecraft.config;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = OutOfPlacecraftMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {
	public static final ClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final CommonConfig COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final ServerConfig SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	static {
		final Pair<ClientConfig, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT = client.getLeft();
		CLIENT_SPEC = client.getRight();

		final Pair<CommonConfig, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON = common.getLeft();
		COMMON_SPEC = common.getRight();

		final Pair<ServerConfig, ForgeConfigSpec> server = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
		SERVER = server.getLeft();
		SERVER_SPEC = server.getRight();
	}
}
