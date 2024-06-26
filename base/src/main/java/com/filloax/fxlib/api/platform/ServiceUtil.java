package com.filloax.fxlib.api.platform;

// Originally from Botania https://botaniamod.net/license.html
// Specifically https://github.com/VazkiiMods/Botania/blob/1.20.x/Xplat/src/main/java/vazkii/botania/api/ServiceUtil.java

import com.filloax.fxlib.FxLib;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ServiceUtil {
    /**
     * @param defaultImpl If nonnull, will be used as a default if no implementations were found.
     *                    Otherwise if it is null and no implementations were found, this method will throw.
     */
    public static <T> T findService(Class<T> clazz, @Nullable Supplier<T> defaultImpl) {
        var providers = ServiceLoader.load(clazz).stream().toList();
        if (providers.isEmpty() && defaultImpl != null) {
            return defaultImpl.get();
        } else if (providers.size() != 1) {
            var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
            var msg = "There should be exactly one implementation of %s on the classpath. Found: %s"
                    .formatted(clazz.getName(), names);
            throw new IllegalStateException(msg);
        } else {
            var provider = providers.get(0);
            FxLib.logger.debug("Instantiating {} for service {}", provider.type().getName(), clazz.getName());
            return provider.get();
        }
    }

    public static <T> T findService(Class<T> clazz) {
        return findService(clazz, null);
    }
}
