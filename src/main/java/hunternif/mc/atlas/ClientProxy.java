package hunternif.mc.atlas;

import static hunternif.mc.atlas.client.StandardTextureSet.*;
import static net.minecraft.world.biome.BiomeGenBase.*;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.BiomeAPI;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.BiomeTextureConfig;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.marker.MarkerTextureConfig;
import hunternif.mc.atlas.marker.MarkerTextureMap;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {
	private BiomeTextureMap biomeTextureMap;
	private BiomeTextureConfig biomeTextureConfig;
	private MarkerTextureMap markerTextureMap;
	private MarkerTextureConfig markerTextureConfig;
	
	private GuiAtlas guiAtlas;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		biomeTextureMap = BiomeTextureMap.instance();
		biomeTextureConfig = new BiomeTextureConfig(new File(configDir, "textures.json"));
		// Assign default values before the config file loads, possibly overwriting them:
		assignVanillaTextures();
		registerVillageTiles();
		biomeTextureConfig.load(biomeTextureMap);
		// Prevent rewriting of the config while there haven't been any changes made:
		biomeTextureMap.setDirty(false);
		
		markerTextureMap = MarkerTextureMap.instance();
		markerTextureConfig = new MarkerTextureConfig(new File(configDir, "marker_textures.json"));
		// Assign default values before the config file loads, possibly overwriting them:
		registerDefaultMarker();
		markerTextureConfig.load(markerTextureMap);
		// Prevent rewriting of the config while there haven't been any changes made:
		markerTextureMap.setDirty(false);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		guiAtlas = new GuiAtlas();
		
		
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	@Override
	public void openAtlasGUI(ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null) { // In-game screen
			guiAtlas.updateL18n();
			mc.displayGuiScreen(guiAtlas.setAtlasItemStack(stack));
		}
	}
	
	/** Assign default textures to vanilla biomes. */
	private void assignVanillaTextures() {
		BiomeAPI api = AtlasAPI.getBiomeAPI();
		api.setTexture(ocean,			WATER);
		api.setTexture(river,			WATER);
		api.setTexture(frozenOcean,	FROZEN_WATER);
		api.setTexture(frozenRiver,	FROZEN_WATER);
		api.setTexture(beach,			BEACH);
		api.setTexture(desert,		SAND);
		api.setTexture(plains,		PLAINS);
		api.setTexture(icePlains,	SNOW);
		api.setTexture(jungleHills,	JUNGLE_HILLS);
		api.setTexture(forestHills,	FOREST_HILLS);
		api.setTexture(desertHills,	HILLS);
		api.setTexture(extremeHills,	MOUNTAINS);
		api.setTexture(extremeHillsEdge, MOUNTAINS);
		api.setTexture(iceMountains,	MOUNTAINS);
		api.setTexture(forest,		FOREST);
		api.setTexture(jungle,		JUNGLE);
		api.setTexture(taiga,			PINES);
		api.setTexture(taigaHills,	PINES_HILLS);
		api.setTexture(swampland,		SWAMP);
		api.setTexture(sky,			BEACH);
		//api.addTexture(hell,		NETHER);
		api.setTexture(mushroomIsland, MUSHROOM);
		api.setTexture(mushroomIslandShore, BEACH);
	}
	
	/** Load default marker textures. */
	private void registerDefaultMarker() {;
		MarkerAPI api = AtlasAPI.getMarkerAPI();
		api.setTexture("google", Textures.MARKER_GOOGLE_MARKER);
		api.setTexture("red_x_large", Textures.MARKER_RED_X_LARGE);
		api.setTexture("red_x_small", Textures.MARKER_RED_X_SMALL);
		api.setTexture("village", Textures.MARKER_VILLAGE);
	}
	
	/** Assign default textures to the pseudo-biomes designating village houses
	 * and territory. */
	private void registerVillageTiles() {
		TileAPI api = AtlasAPI.getTileAPI();
		api.setTexture(ExtTileIdMap.TILE_VILLAGE_HOUSE, StandardTextureSet.HOUSE);
		api.setTexture(ExtTileIdMap.TILE_VILLAGE_TERRITORY, StandardTextureSet.VILLAGE_FENCE);
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
	}
	
	/** Checks if any of the configs's data has been marked dirty and saves it. */
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (biomeTextureMap.isDirty()) {
			AntiqueAtlasMod.logger.info("Saving biome texture config");
			biomeTextureConfig.save(biomeTextureMap);
			biomeTextureMap.setDirty(false);
		}
		if (markerTextureMap.isDirty()) {
			AntiqueAtlasMod.logger.info("Saving marker texture config");
			markerTextureConfig.save(markerTextureMap);
			markerTextureMap.setDirty(false);
		}
	}
}