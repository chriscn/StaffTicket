package github.chriscn;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffTicket extends JavaPlugin {

    PluginDescriptionFile pdfFile = this.getDescription();

    @Override
    public void onEnable() {
        getLogger().info("Enabling " + pdfFile.getName() + " v" + pdfFile.getVersion());
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling " + pdfFile.getName() + " v" + pdfFile.getVersion());
    }
}
