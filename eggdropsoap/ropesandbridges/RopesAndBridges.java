package eggdropsoap.ropesandbridges;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler; // used in 1.6.2
//import cpw.mods.fml.common.Mod.PreInit;    // used in 1.5.2
//import cpw.mods.fml.common.Mod.Init;       // used in 1.5.2
//import cpw.mods.fml.common.Mod.PostInit;   // used in 1.5.2
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid="RopesAndBridges", name="Ropes and Bridges", version="0.1.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class RopesAndBridges {

    // The instance of your mod that Forge uses.
    @Instance("RopesAndBridges")
    public static RopesAndBridges instance;
   
    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="eggdropsoap.ropesandbridges.client.ClientProxy", serverSide="eggdropsoap.ropesandbridges.CommonProxy")
    public static CommonProxy proxy;
   
    @EventHandler // used in 1.6.2
    //@PreInit    // used in 1.5.2
    public void preInit(FMLPreInitializationEvent event) {
            // Stub Method
//    	FreeHangingRopeCurve rope1 = new FreeHangingRopeCurve(28, 0, 10, 0, 10, 15, 20);
//    	FreeHangingRopeCurve rope2 = new FreeHangingRopeCurve(30, 10, 10, 0, 10, 15, 20);
    }
   
    @EventHandler // used in 1.6.2
    //@Init       // used in 1.5.2
    public void load(FMLInitializationEvent event) {
            proxy.registerRenderers();
    }
   
    @EventHandler // used in 1.6.2
    //@PostInit   // used in 1.5.2
    public void postInit(FMLPostInitializationEvent event) {
            // Stub Method
    }
}
