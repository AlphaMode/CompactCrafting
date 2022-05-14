package dev.compactmods.crafting.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.compactmods.crafting.CompactCrafting;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.TimeUnit;

public class CraftingCommandRoot {

    static Disposable PREV;

    public static void onCommandsRegister(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
        register(dispatcher);
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> root = LiteralArgumentBuilder.literal(CompactCrafting.MOD_ID);

        root.then(FieldInfoCommand.create());

        dispatcher.register(root);
    }

    private static int test(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final ServerPlayer player = ctx.getSource().getPlayerOrException();

        if(PREV != null && !PREV.isDisposed()) {
            PREV.dispose();
            PREV = null;
        }
        ctx.getSource().getServer().submitAsync(() -> {

            PublishSubject<Integer> l = PublishSubject.create();
            PREV = l.buffer(500, TimeUnit.MILLISECONDS)
                    .subscribe(times -> {
                        if (!times.isEmpty())
                            player.sendMessage(new TextComponent(String.join(",", times.stream()
                                    .map(Object::toString).toArray(String[]::new))), ChatType.CHAT, player.getUUID());
                    }, err -> {
                        CompactCrafting.LOGGER.debug("error");
                    }, () -> {
                        player.sendMessage(new TextComponent("done"), ChatType.CHAT, player.getUUID());
                    });

            player.server.submitAsync(() -> {
                for (int i = 1; i <= 100; i++) {
                    l.onNext(i);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        return 0;
    }
}
