/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.LanguageChangeEvent;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin {
    @Inject(method = "setLanguage", at = @At("HEAD"), cancellable = true)
    private void onSetLanguage(String languageCode, CallbackInfo ci) {
        LanguageChangeEvent event = MeteorClient.EVENT_BUS.post(new LanguageChangeEvent(languageCode));
        if (event.isCancelled()) ci.cancel();
    }
}
