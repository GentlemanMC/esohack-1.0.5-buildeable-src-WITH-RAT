package com.esoterik.client.mixin.mixins;

import com.esoterik.client.features.modules.movement.NoSlowDown;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BlockSoulSand.class})
public class MixinBlockSoulSand extends Block {
   public MixinBlockSoulSand() {
      super(Material.field_151595_p, MapColor.field_151650_B);
   }

   @Inject(
      method = {"onEntityCollision"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onEntityCollisionHook(World worldIn, BlockPos pos, IBlockState state, Entity entityIn, CallbackInfo info) {
      if (NoSlowDown.getInstance().isOn() && (Boolean)NoSlowDown.getInstance().soulSand.getValue()) {
         info.cancel();
      }

   }
}
