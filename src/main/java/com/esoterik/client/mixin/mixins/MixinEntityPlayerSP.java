package com.esoterik.client.mixin.mixins;

import com.esoterik.client.esohack;
import com.esoterik.client.event.events.ChatEvent;
import com.esoterik.client.event.events.MoveEvent;
import com.esoterik.client.event.events.PushEvent;
import com.esoterik.client.event.events.UpdateWalkingPlayerEvent;
import com.esoterik.client.features.modules.movement.Sprint;
import com.esoterik.client.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {EntityPlayerSP.class},
   priority = 9998
)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
   public MixinEntityPlayerSP(Minecraft p_i47378_1_, World p_i47378_2_, NetHandlerPlayClient p_i47378_3_, StatisticsManager p_i47378_4_, RecipeBook p_i47378_5_) {
      super(p_i47378_2_, p_i47378_3_.func_175105_e());
   }

   @Inject(
      method = {"sendChatMessage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void sendChatMessage(String message, CallbackInfo callback) {
      ChatEvent chatEvent = new ChatEvent(message);
      MinecraftForge.EVENT_BUS.post(chatEvent);
   }

   @Redirect(
      method = {"onLivingUpdate"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/entity/EntityPlayerSP;closeScreen()V"
)
   )
   public void closeScreenHook(EntityPlayerSP entityPlayerSP) {
   }

   @Redirect(
      method = {"onLivingUpdate"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"
)
   )
   public void displayGuiScreenHook(Minecraft mc, GuiScreen screen) {
   }

   @Redirect(
      method = {"onLivingUpdate"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/entity/EntityPlayerSP;setSprinting(Z)V",
   ordinal = 2
)
   )
   public void onLivingUpdate(EntityPlayerSP entityPlayerSP, boolean sprinting) {
      if (!Sprint.getInstance().isOn() || Sprint.getInstance().mode.getValue() != Sprint.Mode.RAGE || Util.mc.field_71439_g.field_71158_b.field_192832_b == 0.0F && Util.mc.field_71439_g.field_71158_b.field_78902_a == 0.0F) {
         entityPlayerSP.func_70031_b(sprinting);
      } else {
         entityPlayerSP.func_70031_b(true);
      }

   }

   @Inject(
      method = {"pushOutOfBlocks"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pushOutOfBlocksHook(double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
      PushEvent event = new PushEvent(1);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"onUpdateWalkingPlayer"},
      at = {@At("HEAD")}
   )
   private void preMotion(CallbackInfo info) {
      UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(0);
      MinecraftForge.EVENT_BUS.post(event);
   }

   @Inject(
      method = {"onUpdateWalkingPlayer"},
      at = {@At("RETURN")}
   )
   private void postMotion(CallbackInfo info) {
      UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(1);
      MinecraftForge.EVENT_BUS.post(event);
   }

   @Inject(
      method = {"Lnet/minecraft/client/entity/EntityPlayerSP;setServerBrand(Ljava/lang/String;)V"},
      at = {@At("HEAD")}
   )
   public void getBrand(String brand, CallbackInfo callbackInfo) {
      if (esohack.serverManager != null) {
         esohack.serverManager.setServerBrand(brand);
      }

   }

   @Redirect(
      method = {"move"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"
)
   )
   public void move(AbstractClientPlayer player, MoverType moverType, double x, double y, double z) {
      MoveEvent event = new MoveEvent(0, moverType, x, y, z);
      MinecraftForge.EVENT_BUS.post(event);
      if (!event.isCanceled()) {
         super.func_70091_d(event.getType(), event.getX(), event.getY(), event.getZ());
      }

   }
}
