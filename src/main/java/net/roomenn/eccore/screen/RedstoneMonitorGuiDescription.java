package net.roomenn.eccore.screen;

import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.block.trigger.RedstoneMonitorBlockEntity;
import net.roomenn.eccore.screen.widget.FlushLogButton;
import net.roomenn.eccore.screen.widget.IntTextField;
import net.roomenn.eccore.screen.widget.SensorButton;
import net.roomenn.eccore.screen.widget.TooltipButton;

import java.util.List;

import static net.roomenn.eccore.utils.Constants.*;

public class RedstoneMonitorGuiDescription extends SyncedGuiDescription {

    private final List<WWidget> logButtons = Lists.newArrayList();
    private final List<WWidget> triggerButtons = Lists.newArrayList();
    private final List<WWidget> targetButtons = Lists.newArrayList();
    private final BlockPos monitorPos;
    private RedstoneMonitorBlockEntity monitor;
    private SensorButton sensorButton;
    private int[] syncedIntArray;
    private boolean isSensor;

    public RedstoneMonitorGuiDescription(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, BlockPos.ORIGIN);
    }

    public RedstoneMonitorGuiDescription(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        super(ModScreenHandlers.REDSTONE_MONITOR_SCREEN_HANDLER_TYPE, syncId, playerInventory);

        this.monitorPos = pos;
        if (!world.isClient && getMonitor() != null) {
            this.syncedIntArray = monitor.getSettingArray();
            this.isSensor = monitor.getSensor();
        }

        networking();

        WGridPanel root = rootWidget();
        root.validate(this);
    }

    /* WIDGETS */

    private WGridPanel rootWidget() {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(264, 180);
        root.setInsets(Insets.ROOT_PANEL);

        WSprite guiTexture = new WSprite(new Identifier(ECCore.MOD_ID, "textures/gui/monitor_gui.png"));
        //root.add(guiTexture, 0,0,20,13);

        WButton flush = new FlushLogButton();
        sensorButton = new SensorButton();
        root.add(flush, 10,0);
        root.add(sensorButton, 11,0);

        root.add(logWidget(),1,1);
        root.add(triggerWidget(), 1,4);
        root.add(targetWidget(), 1,7);

        return root;
    }

    private WGridPanel logWidget() {
        WGridPanel log = new WGridPanel();

        WLabel label = new WLabel(Text.translatable("gui."+ECCore.MOD_ID+".monitor.logs_label"));
        WButton noLog = new TooltipButton("monitor.no_log", Items.BARRIER,0);
        WButton oneTimeLog = new TooltipButton("monitor.one_time_log", Items.WOODEN_PICKAXE,1);
        WButton timeoutLog = new TooltipButton("monitor.timeout_log", Items.STONE_PICKAXE,2);
        WTextField timeoutField = new IntTextField(Text.translatable("gui."+ECCore.MOD_ID+".monitor.timeout_field"));
        WLabel fieldLabel = new WLabel(Text.translatable("gui."+ECCore.MOD_ID+".monitor.seconds"));

        if (world.isClient) {
            logButtons.add(noLog);
            logButtons.add(oneTimeLog);
            logButtons.add(timeoutLog);
            logButtons.add(timeoutField);
        }

        log.add(label, 0,0);
        log.add(noLog, 1,1);
        log.add(oneTimeLog, 2,1);
        log.add(timeoutLog, 3,1);
        log.add(timeoutField, 5,1,3,1);
        log.add(fieldLabel, 8,1);

        return log;
    }

    private WGridPanel triggerWidget() {
        WGridPanel trigger = new WGridPanel();

        WLabel label = new WLabel(Text.translatable("gui."+ECCore.MOD_ID+".redstone_monitor.trigger_label"));
        WButton holdTrigger = new TooltipButton("redstone_monitor.hold_trigger", Items.OAK_PRESSURE_PLATE,0);
        WButton timedTrigger = new TooltipButton("redstone_monitor.timed_trigger", Items.OAK_BUTTON,1);
        WButton holdTimedTrigger = new TooltipButton("redstone_monitor.hold_timed_trigger", Items.REDSTONE,2);
        WTextField timedField = new IntTextField(Text.translatable("gui."+ECCore.MOD_ID+".redstone_monitor.timed_field"));
        WLabel fieldLabel = new WLabel(Text.translatable("gui."+ECCore.MOD_ID+".monitor.ticks"));

        if (world.isClient) {
            triggerButtons.add(holdTrigger);
            triggerButtons.add(timedTrigger);
            triggerButtons.add(holdTimedTrigger);
            triggerButtons.add(timedField);
        }

        trigger.add(label, 0,0);
        trigger.add(holdTrigger, 1,1);
        trigger.add(timedTrigger, 2,1);
        trigger.add(holdTimedTrigger, 3,1);
        trigger.add(timedField, 5,1,3,1);
        trigger.add(fieldLabel, 8,1);

        return trigger;
    }

    private WGridPanel targetWidget() {
        WGridPanel target = new WGridPanel();

        WLabel label = new WLabel(Text.translatable("gui."+ECCore.MOD_ID+".redstone_monitor.target_label"));
        TooltipButton playerButton = new TooltipButton("redstone_monitor.player", Items.PLAYER_HEAD);
        TooltipButton mobButton = new TooltipButton("redstone_monitor.mob", Items.CREEPER_HEAD);
        TooltipButton entityButton = new TooltipButton("redstone_monitor.entity", Items.DIAMOND);

        if (world.isClient) {
            targetButtons.add(playerButton);
            targetButtons.add(mobButton);
            targetButtons.add(entityButton);
        }

        target.add(label, 0,0);
        target.add(playerButton, 1, 1);
        target.add(mobButton, 2, 1);
        target.add(entityButton, 3, 1);

        return target;
    }

    @Environment(EnvType.CLIENT)
    private void populateWidget(int index, WWidget widget, List<WWidget> list){

        if (widget instanceof TooltipButton button) {
            button.init(Property.create(syncedIntArray, index), list);
        } else if (widget instanceof IntTextField field) {
            field.init(Property.create(syncedIntArray, index));
        }
    }

    @Environment(EnvType.CLIENT)
    private void initWidgets(){
        populateWidget(0, logButtons.get(0), logButtons);
        populateWidget(0, logButtons.get(1), logButtons);
        populateWidget(0, logButtons.get(2), logButtons);
        populateWidget(1, logButtons.get(3), logButtons);

        populateWidget(2, triggerButtons.get(0), triggerButtons);
        populateWidget(2, triggerButtons.get(1), triggerButtons);
        populateWidget(2, triggerButtons.get(2), triggerButtons);
        populateWidget(3, triggerButtons.get(3), triggerButtons);

        populateWidget(4, targetButtons.get(0), null);
        populateWidget(5, targetButtons.get(1), null);
        populateWidget(6, targetButtons.get(2), null);

        sensorButton.init(isSensor);
    }

    /* NETWORKING */

    private void networking() {
        if(world.isClient) {
            clientNetworking();
        } else {
            serverNetworking();
        }
    }

    private void setSyncedIntArray(int[] array) {
        syncedIntArray = array;
    }


    private void clientNetworking() {
        ScreenNetworking.of(this, NetworkSide.CLIENT).send(READY_MESSAGE_ID, buf -> {});

        ScreenNetworking.of(this, NetworkSide.CLIENT).receive(INIT_MESSAGE_ID, buf -> {
            setSyncedIntArray(buf.readIntArray());
            isSensor = buf.readBoolean();
            initWidgets();
        });
    }

    private void serverNetworking() {
        ScreenNetworking.of(this, NetworkSide.SERVER).receive(READY_MESSAGE_ID, b ->
                ScreenNetworking.of(this, NetworkSide.SERVER).send(INIT_MESSAGE_ID, buf -> {
                    buf.writeIntArray(syncedIntArray);
                    buf.writeBoolean(isSensor);
        }));

        ScreenNetworking.of(this, NetworkSide.SERVER).receive(UPDATE_DATA_MESSAGE_ID, buf -> {
            if (this.getMonitor() != null) {
                monitor.setSettingArray(buf.readIntArray());
            }
        });

        ScreenNetworking.of(this, NetworkSide.SERVER).receive(FLUSH_LOGS_MESSAGE_ID, buf -> {
            if (this.getMonitor() != null) {
                monitor.flushLogs();
            }
        });

        ScreenNetworking.of(this, NetworkSide.SERVER).receive(IS_SENSOR_MESSAGE_ID, buf -> {
            if (this.getMonitor() != null) {
                monitor.setSensor(buf.readBoolean());
            }
        });

    }

    @Environment(EnvType.CLIENT)
    public void saveData(){
        if (world.isClient) {
            ScreenNetworking.of(this, NetworkSide.CLIENT).send(UPDATE_DATA_MESSAGE_ID, buf ->
                    buf.writeIntArray(syncedIntArray));
        }
    }

    @Environment(EnvType.CLIENT)
    public void flushLogs(){
        if (world.isClient) {
            ScreenNetworking.of(this, NetworkSide.CLIENT).send(FLUSH_LOGS_MESSAGE_ID, buf -> {});
        }
    }

    @Environment(EnvType.CLIENT)
    public void setIsSensor(boolean bl){
        isSensor = bl;
        if (world.isClient) {
            ScreenNetworking.of(this, NetworkSide.CLIENT).send(IS_SENSOR_MESSAGE_ID, buf ->
                    buf.writeBoolean(isSensor));
        }
    }

    private RedstoneMonitorBlockEntity getMonitor(){
        if (!world.isClient) {
            if (monitor == null) {
                BlockEntity be = world.getBlockEntity(monitorPos);
                if (be instanceof RedstoneMonitorBlockEntity monitor) this.monitor = monitor;
            }
            return monitor;
        }
        return null;
    }

}
