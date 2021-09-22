package com.esoterik.client.features.modules.client;

import com.esoterik.client.esohack;
import com.esoterik.client.event.events.ClientEvent;
import com.esoterik.client.features.command.Command;
import com.esoterik.client.features.modules.Module;
import com.esoterik.client.features.setting.Setting;
import java.awt.GraphicsEnvironment;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FontMod extends Module {
   private boolean reloadFont = false;
   public Setting<String> fontName = this.register(new Setting("FontName", "Arial", "Name of the font."));
   public Setting<Integer> fontSize = this.register(new Setting("FontSize", 18, "Size of the font."));
   public Setting<Integer> fontStyle = this.register(new Setting("FontStyle", 0, "Style of the font."));
   public Setting<Boolean> antiAlias = this.register(new Setting("AntiAlias", true, "Smoother font."));
   public Setting<Boolean> fractionalMetrics = this.register(new Setting("Metrics", true, "Thinner font."));
   public Setting<Boolean> shadow = this.register(new Setting("Shadow", true, "Less shadow offset font."));
   public Setting<Boolean> showFonts = this.register(new Setting("Fonts", false, "Shows all fonts."));
   private static FontMod INSTANCE = new FontMod();

   public FontMod() {
      super("CustomFont", "CustomFont for all of the clients text. Use the font command.", Module.Category.CLIENT, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static FontMod getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new FontMod();
      }

      return INSTANCE;
   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (event.getStage() == 2) {
         Setting setting = event.getSetting();
         if (setting != null && setting.getFeature().equals(this)) {
            if (setting.getName().equals("FontName") && !checkFont(setting.getPlannedValue().toString(), false)) {
               Command.sendMessage("§cThat font doesnt exist.");
               event.setCanceled(true);
               return;
            }

            this.reloadFont = true;
         }
      }

   }

   public void onTick() {
      if ((Boolean)this.showFonts.getValue()) {
         checkFont("Hello", true);
         Command.sendMessage("Current Font: " + (String)this.fontName.getValue());
         this.showFonts.setValue(false);
      }

      if (this.reloadFont) {
         esohack.textManager.init(false);
         this.reloadFont = false;
      }

   }

   public static boolean checkFont(String font, boolean message) {
      String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
      String[] var3 = fonts;
      int var4 = fonts.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String s = var3[var5];
         if (!message && s.equals(font)) {
            return true;
         }

         if (message) {
            Command.sendMessage(s);
         }
      }

      return false;
   }
}
