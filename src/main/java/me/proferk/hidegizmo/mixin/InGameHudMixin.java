package me.proferk.hidegizmo.mixin;

import me.proferk.hidegizmo.HideGizmo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @ModifyVariable(method = "renderCrosshair(Lnet/minecraft/client/gui/DrawContext;)V", at = @At("STORE"), ordinal = 0)
    private GameOptions disableDebugEnabled(GameOptions gameOptions) {
        if(gameOptions.debugEnabled) {
            // if debug menu enabled, spoof it so that renderCrosshair() renders the default crosshair instead
            gameOptions.debugEnabled = false;
            HideGizmo.spoofed = true;
        }
        return gameOptions;
    }

    @Inject(method = "renderCrosshair(Lnet/minecraft/client/gui/DrawContext;)V", at = @At("TAIL"))
    private void enableDebugEnabled(DrawContext context, CallbackInfo ci) {
        GameOptions gameOptions = ((InGameHudAccessor) this).getClient().options;

        // after renderCrosshair() has rendered the crosshair, unspoof debugEnabled so that other functions that access debugEnabled get the actual value.
        if(HideGizmo.spoofed && !gameOptions.debugEnabled) {
            gameOptions.debugEnabled = true;
            HideGizmo.spoofed = false;
        }
    }
}
