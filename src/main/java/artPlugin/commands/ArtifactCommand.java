package artPlugin.commands;

import artPlugin.Main;
import artPlugin.models.Artifact;
import artPlugin.models.Boss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArtifactCommand implements CommandExecutor {
    private final Main plugin;

    public ArtifactCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6=== Artifact Plugin Commands ===");
            sender.sendMessage("§e/artifact reload §7- Reload all configurations");
            sender.sendMessage("§e/artifact give <artifact_id> [player] §7- Give artifact to player");
            sender.sendMessage("§e/artifact list §7- List all artifacts");
            sender.sendMessage("§e/artifact spawn <boss_id> §7- Spawn a boss");
            sender.sendMessage("§e/artifact listbosses §7- List all bosses");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("artifact.admin")) {
                    sender.sendMessage("§cNo permission!");
                    return true;
                }
                plugin.getArtifactManager().loadArtifacts();
                plugin.getBossManager().loadBosses();
                sender.sendMessage("§aConfigurations reloaded!");
                break;

            case "give":
                if (!(sender instanceof Player) && args.length < 3) {
                    sender.sendMessage("§cUsage: /artifact give <artifact_id> <player>");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /artifact give <artifact_id> [player]");
                    return true;
                }

                Player target = (sender instanceof Player) ? (Player) sender : null;
                if (args.length >= 3) {
                    target = plugin.getServer().getPlayer(args[2]);
                }

                if (target == null) {
                    sender.sendMessage("§cPlayer not found!");
                    return true;
                }

                plugin.getArtifactManager().giveArtifactToPlayer(target, args[1]);
                if (!sender.equals(target)) {
                    sender.sendMessage("§aGave artifact " + args[1] + " to " + target.getName());
                }
                break;

            case "list":
                sender.sendMessage("§6=== Available Artifacts ===");
                for (Artifact artifact : plugin.getArtifactManager().getArtifacts().values()) {
                    sender.sendMessage("§e" + artifact.getId() + " §7- §f" + artifact.getName());
                }
                break;

            case "spawn":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cOnly players can spawn bosses!");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /artifact spawn <boss_id>");
                    return true;
                }

                Player player = (Player) sender;
                if (plugin.getBossManager().spawnBoss(args[1]) != null) {
                    sender.sendMessage("§aSpawned boss: " + args[1]);
                } else {
                    sender.sendMessage("§cBoss not found: " + args[1]);
                }
                break;

            case "listbosses":
                sender.sendMessage("§6=== Available Bosses ===");
                for (Boss boss : plugin.getBossManager().getBosses().values()) {
                    sender.sendMessage("§e" + boss.getId() + " §7- §f" + boss.getName());
                }
                break;

            default:
                sender.sendMessage("§cUnknown subcommand: " + args[0]);
                return false;
        }

        return true;
    }
}