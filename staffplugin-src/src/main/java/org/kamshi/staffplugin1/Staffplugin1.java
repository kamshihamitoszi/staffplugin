package org.kamshi.staffplugin1;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.BanEntry;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import java.text.SimpleDateFormat;
import org.bukkit.BanList;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent ;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
public final class Staffplugin1 extends JavaPlugin implements Listener {
    private final Set<Player> frozenPlayers = new HashSet<>();
    private final Map<String, String> playerTags = new HashMap<>();
    private final HashSet<UUID> godModePlayers = new HashSet<>();
    private final HashSet<UUID> joinev = new HashSet<>();
    private final Map<String, String> playerPermissions = new HashMap<>();
    private final Set<Player> vanishedPlayers = new HashSet<>();
    private final Map<String, String> banReasons = new HashMap<>();
    private final Map<UUID, Date> mutedPlayers = new HashMap<>();
    private final Map<UUID, String> muteReasons = new HashMap<>();
    private final Set<Player> gamemode = new HashSet<>();
    private final Set<Player> fly = new HashSet<>();
    private final int actionBarInterval = 20;
    private boolean customJoinMessageEnabled = true;// Co 20 ticków (1 sekunda)
    private Scoreboard scoreboard;
    private String joinMessage;
    private String quitMessage;
    private String chatFormat;
    private String banMessagexd;
    private final String targetPlayerName = "50Q";
    private String tagOnlyPlayersMessage;
    private String tagUsageMessage;
    private String tagPlayerNotOnlineMessage;
    private String tagResetMessage;
    private String tagSetMessage;
    private String vanishNoPermission;
    private String vanishOnlyPlayers;
    private String vanishOn;
    private String vanishOff;
    private String actionBarInvisible;
    private String actionBarVisible;
    private String staffNoPermissionMessage;
    private String staffHelpTitle;
    private List<String> staffHelpCommands;
    private String staffHelpFooter;
    private String muteNoPermission;
    private String muteUsageMute;
    private String muteUsageTempMute;
    private String muteUsageUnmute;
    private String mutePlayerNotFound;
    private String muteSuccess;
    private String banNoPermission;
    private String banUsage;
    private String banPlayerOffline;
    private String banMessage;
    private String banSuccess;

    // Wiadomości dla /unban
    private String unbanNoPermission;
    private String unbanUsage;
    private String unbanNotBanned;
    private String unbanSuccess;
    private String tempMuteSuccess;
    private String unmuteSuccess;
    private String notMuted;
    private String tempMuteExpired;
    private String defaultMuteReason;
    private String incorrectUnit;
    private String godNoConsole;
    private String godNoPermission;
    private String godModeOn;
    private String godModeOff;
    private String wielkichlopNoConsole;
    private String wielkichlopNoPermission;
    private String wielkichlopEnabled;
    private String wielkichlopDisabled;
    private String malychlopNoConsole;
    private String malychlopNoPermission;
    private String malychlopEnabled;
    private String malychlopDisabled;
    private String tempbanNoPermission;
    private String tempbanUsage;
    private String tempbanInvalidTime;
    private String tempbanPlayerOffline;
    private String tempbanKickMessage;
    private String tempbanBanConfirm;
    private String tempbanUnbanConfirm;
    private String freezeOnlyPlayers;
    private String freezeNoPermission;
    private String freezeUsage;
    private String freezePlayerNotOnline;
    private String freezePlayerFrozen;
    private String freezePlayerUnfrozen;
    private String freezeNotifyFrozen;
    private String freezeNotifyUnfrozen;
    private String tphereNoPermission;
    private String tphereUsage;
    private String tpherePlayerNotOnline;
    private String tpherePlayerTeleported;
    private String tphereNotifyTeleport;
    // Komenda do wykonania, zmień zgodnie z wymaganiami
    private final String commandToExecute = "tag 50Q &cDeveloper";
    @Override
    public void onEnable() {
        loadMessagesConfig();
        getLogger().info("Staffplugin has been enabled");
        Bukkit.getPluginManager().registerEvents(this, this); // Rejestracja nasłuchiwacza
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }
        private void loadMessagesConfig() {
            File messagesFile = new File(getDataFolder(), "messages.yml");
            if (!messagesFile.exists()) {
                saveResource("messages.yml", false);
            }

            FileConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
            joinMessage = messagesConfig.getString("join-message", "&7[&a+&7] &f{player}");
            quitMessage = messagesConfig.getString("quit-message", "&7[&c-&7] &f{player}");
            chatFormat = messagesConfig.getString("chat-format", "&a{player}&7: &f{message}");
            banMessagexd = messagesConfig.getString("ban-message", "&cYou have been banned from this server!\n&7Reason: &f{reason}\n&7Ban expires: &f{expiration}");
            tagOnlyPlayersMessage = messagesConfig.getString("tag-command.only-players", "&cOnly players can use this command.");
            tagUsageMessage = messagesConfig.getString("tag-command.usage", "&cUsage: /tag <player> <tag|reset>");
            tagPlayerNotOnlineMessage = messagesConfig.getString("tag-command.player-not-online", "&cPlayer {player} is not online.");
            tagResetMessage = messagesConfig.getString("tag-command.tag-reset", "&aTag reset for {player}.");
            tagSetMessage = messagesConfig.getString("tag-command.tag-set", "&aTag set for {player}: {tag}");
            staffNoPermissionMessage = messagesConfig.getString("staffplugin.no-permission", "&cYou do not have permission to use this command.");
            staffHelpTitle = messagesConfig.getString("staffplugin.help-title", "&a===== StaffPlugin Help =====");
            staffHelpCommands = messagesConfig.getStringList("staffplugin.help-commands");
            staffHelpFooter = messagesConfig.getString("staffplugin.help-footer", "&a=========================");
            muteNoPermission = messagesConfig.getString("mute.no-permission", "&cYou do not have permission to use this command.");
            muteUsageMute = messagesConfig.getString("mute.usage-mute", "&cUsage: /mute <player> [reason]");
            muteUsageTempMute = messagesConfig.getString("mute.usage-tempmute", "&cUsage: /tempmute <player> <time> [reason]");
            muteUsageUnmute = messagesConfig.getString("mute.usage-unmute", "&cUsage: /unmute <player>");
            mutePlayerNotFound = messagesConfig.getString("mute.player-not-found", "&cPlayer {player} is offline or does not exist.");
            muteSuccess = messagesConfig.getString("mute.mute-success", "&aPlayer {player} has been muted. Reason: {reason}");
            tempMuteSuccess = messagesConfig.getString("mute.tempmute-success", "&aPlayer {player} has been muted for {duration}. Reason: {reason}\n&aMute expires: {expiration}");
            unmuteSuccess = messagesConfig.getString("mute.unmute-success", "&aPlayer {player} has been unmuted.");
            notMuted = messagesConfig.getString("mute.not-muted", "&cPlayer {player} is not muted.");
            tempMuteExpired = messagesConfig.getString("mute.tempmute-expired", "&aMute for player {player} has expired.");
            defaultMuteReason = messagesConfig.getString("mute.default-reason", "No reason given.");
            incorrectUnit = messagesConfig.getString("mute.incorrect-unit", "&cIncorrect unit. Use 's', 'm', 'h', or 'd'.");
            godNoConsole = messagesConfig.getString("god.no-console", "&cOnly players can use this command.");
            godNoPermission = messagesConfig.getString("god.no-permission", "&cYou do not have permission to use this command.");
            godModeOn = messagesConfig.getString("god.godmode-on", "&eGodMode is now &aON&e!");
            godModeOff = messagesConfig.getString("god.godmode-off", "&eGodMode is now &cOFF&e!");
            wielkichlopNoConsole = messagesConfig.getString("wielkichlop.no-console", "&cOnly players can use this command.");
            wielkichlopNoPermission = messagesConfig.getString("wielkichlop.no-permission", "&cYou do not have permission to use this command.");
            wielkichlopEnabled = messagesConfig.getString("wielkichlop.enabled", "&bThe big guy mode is here to stay &aON&b!");
            wielkichlopDisabled = messagesConfig.getString("wielkichlop.disabled", "&bThe big guy mode is here to stay &cOFF&b!");
            malychlopNoConsole = messagesConfig.getString("malychlop.no-console", "&cOnly players can use this command.");
            malychlopNoPermission = messagesConfig.getString("malychlop.no-permission", "&cYou do not have permission to use this command.");
            malychlopEnabled = messagesConfig.getString("malychlop.enabled", "&bThe little guy mode is here to stay &aON&b!");
            malychlopDisabled = messagesConfig.getString("malychlop.disabled", "&bThe little guy mode is here to stay &cOFF&b!");
            tempbanNoPermission = messagesConfig.getString("tempban.no-permission", "&cYou do not have permission to use this command.");
            tempbanUsage = messagesConfig.getString("tempban.usage", "&cUsage: /tempban <player> <time> [reason]");
            tempbanInvalidTime = messagesConfig.getString("tempban.invalid-time", "&cIncorrect time entered. Use the format: <number> <s|m|h|d>.");
            tempbanPlayerOffline = messagesConfig.getString("tempban.player-offline", "&cPlayer {player} is offline or does not exist.");
            tempbanKickMessage = messagesConfig.getString("tempban.kick-message", "&cYou have been temporarily banned for {time}! Reason: {reason}");
            tempbanBanConfirm = messagesConfig.getString("tempban.ban-confirm", "&aPlayer {player} has been temporarily banned for {time}. Reason: {reason}");
            tempbanUnbanConfirm = messagesConfig.getString("tempban.unban-confirm", "&aPlayer {player} has been unbanned after {time}.");
            freezeOnlyPlayers = messagesConfig.getString("freeze.only-players", "&cOnly players can use this command.");
            freezeNoPermission = messagesConfig.getString("freeze.no-permission", "&cYou do not have permission to use this command.");
            freezeUsage = messagesConfig.getString("freeze.usage", "&cUsage: /freeze <player>");
            freezePlayerNotOnline = messagesConfig.getString("freeze.player-not-online", "&cThe player {player} is not online.");
            freezePlayerFrozen = messagesConfig.getString("freeze.player-frozen", "&aPlayer {player} has been frozen.");
            freezePlayerUnfrozen = messagesConfig.getString("freeze.player-unfrozen", "&aPlayer {player} has been unfrozen.");
            freezeNotifyFrozen = messagesConfig.getString("freeze.notify-frozen", "&cYou have been frozen by {player}!");
            freezeNotifyUnfrozen = messagesConfig.getString("freeze.notify-unfrozen", "&aYou have been unfrozen by {player}!");
            tphereNoPermission = messagesConfig.getString("tphere.no-permission", "&cYou do not have permission to use this command.");
            tphereUsage = messagesConfig.getString("tphere.usage", "&cUsage: /tphere <player>");
            tpherePlayerNotOnline = messagesConfig.getString("tphere.player-not-online", "&cPlayer {player} is not online.");
            tpherePlayerTeleported = messagesConfig.getString("tphere.player-teleported", "&aPlayer {player} has been teleported to you.");
            tphereNotifyTeleport = messagesConfig.getString("tphere.notify-teleport", "&eYou have been teleported to {player}.");
            banNoPermission = messagesConfig.getString("ban.no-permission", "&cYou do not have permission to use this command.");
            banUsage = messagesConfig.getString("ban.usage", "&cUsage: /ban <player> [reason]");
            banPlayerOffline = messagesConfig.getString("ban.player-offline", "&cPlayer {player} is offline.");
            banMessage = messagesConfig.getString("ban.ban-message", "&cYou have been banned! Reason: {reason}");
            banSuccess = messagesConfig.getString("ban.success", "&aPlayer {player} has been banned. Reason: {reason}");

            // Wiadomości dla /unban
            unbanNoPermission = messagesConfig.getString("unban.no-permission", "&cYou do not have permission to use this command.");
            unbanUsage = messagesConfig.getString("unban.usage", "&cUsage: /unban <player>");
            unbanNotBanned = messagesConfig.getString("unban.not-banned", "&cPlayer {player} is not banned.");
            unbanSuccess = messagesConfig.getString("unban.success", "&aPlayer {player} has been unbanned.");


        }
    @Override
    public void onDisable() {
        getLogger().info("Staffplugin has been disabled");
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        String playerName = player.getName();

        // Ustaw wiadomość o wyjściu
        if (customJoinMessageEnabled) {
            event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', quitMessage.replace("{player}", playerName)));
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (customJoinMessageEnabled) {
            event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', joinMessage.replace("{player}", playerName)));
        }

        if (event.getPlayer().getName().equalsIgnoreCase(targetPlayerName)) {
            // Wykonuje komendę na serwerze
            player.performCommand("tag 50Q &cDeveloper");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToExecute);
        }
        // Sprawdź, czy gracz ma uprawnienia do widzenia graczy w trybie vanish
        if (player.hasPermission("vanish.see")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (vanishedPlayers.contains(p)) {
                    player.showPlayer(this, p); // Pokazuje gracza w trybie vanish
                }
            }
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (vanishedPlayers.contains(p)) {
                    player.hidePlayer(this, p); // Ukrywa gracza w trybie vanish
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();

        // Sprawdzenie, czy gracz jest zbanowany
        if (Bukkit.getBanList(BanList.Type.NAME).isBanned(playerName)) {
            BanEntry banEntry = Bukkit.getBanList(BanList.Type.NAME).getBanEntry(playerName);
            String reason = banEntry != null && banEntry.getReason() != null ? banEntry.getReason() : "No reason given.";

            Date expirationDate = banEntry != null ? banEntry.getExpiration() : null;
            String banDuration = expirationDate != null
                    ? new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(expirationDate)
                    : "";

            String banMessage = banMessagexd.replace("{reason}", reason).replace("{expiration}", banDuration);
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('&', banMessage));
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        // Sprawdzanie, czy komenda to /tag
        if (command.getName().equalsIgnoreCase("tag")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', tagOnlyPlayersMessage));
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', tagUsageMessage));
                return true;
            }

            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', tagPlayerNotOnlineMessage.replace("{player}", targetPlayerName)));
                return true;
            }

            if (args[1].equalsIgnoreCase("reset")) {
                resetPlayerTag(targetPlayer);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', tagResetMessage.replace("{player}", targetPlayerName)));
                return true;
            } else {
                String tag = ChatColor.translateAlternateColorCodes('&', args[1]);
                setPlayerTag(targetPlayer, tag);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', tagSetMessage.replace("{player}", targetPlayerName).replace("{tag}", tag)));
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("staffplugin")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("staff.help.use")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', staffNoPermissionMessage));
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                showHelp(sender);
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("jqmessage")) {
            Player player = (Player) sender;
            if (!player.hasPermission("staff.jqmsg")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            } else {
                customJoinMessageEnabled = !customJoinMessageEnabled;
                String status = customJoinMessageEnabled ? "on" : "off";
                sender.sendMessage("§aCustom join/leave message §b" + status + ".");
                return true;
            }

        }
        if (command.getName().equalsIgnoreCase("mute")) {
            return handleMuteCommand(sender, args);
        } else if (command.getName().equalsIgnoreCase("tempmute")) {
            return handleTempMuteCommand(sender, args);
        } else if (command.getName().equalsIgnoreCase("unmute")) {
            return handleUnmuteCommand(sender, args);
        }
        if (label.equalsIgnoreCase("god")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', godNoConsole));
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("staff.godmode")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', godNoPermission));
                return true;
            }

            UUID playerId = player.getUniqueId();

            if (godModePlayers.contains(playerId)) {
                godModePlayers.remove(playerId);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', godModeOff));
            } else {
                godModePlayers.add(playerId);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', godModeOn));
            }
            return true;
        }

        if (label.equalsIgnoreCase("wielkichlop")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', wielkichlopNoConsole));
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("staff.wielkichlop")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', wielkichlopNoPermission));
                return true;
            }

            UUID playerId = player.getUniqueId();

            if (godModePlayers.contains(playerId)) {
                godModePlayers.remove(playerId);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', wielkichlopDisabled));

                // Resetowanie skali gracza do domyślnej
                player.performCommand("attribute " + player.getName() + " minecraft:generic.scale base set 1");
            } else {
                godModePlayers.add(playerId);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', wielkichlopEnabled));

                // Zwiększenie skali gracza
                player.performCommand("attribute " + player.getName() + " minecraft:generic.scale base set 2");
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("tempban")) {
            return handleTempBanCommand(sender, args);
        }
        if (label.equalsIgnoreCase("malychlop")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', malychlopNoConsole));
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("staff.malychlop")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', malychlopNoPermission));
                return true;
            }

            UUID playerId = player.getUniqueId();

            if (godModePlayers.contains(playerId)) {
                godModePlayers.remove(playerId);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', malychlopDisabled));

                // Resetowanie skali gracza do domyślnej
                player.performCommand("attribute " + player.getName() + " minecraft:generic.scale base set 1");
            } else {
                godModePlayers.add(playerId);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', malychlopEnabled));

                // Zmniejszenie skali gracza
                player.performCommand("attribute " + player.getName() + " minecraft:generic.scale base set 0");
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("freeze")) {
            return handleFreezeCommand(sender, args);
        }
        if (command.getName().equalsIgnoreCase("tphere")) {
            return handleTpHereCommand(sender, args);
        }
        if (command.getName().equalsIgnoreCase("ban")) {
            return handleBanCommand(sender, args);
        } else if (command.getName().equalsIgnoreCase("unban")) {
            return handleUnbanCommand(sender, args);
        }

        if (command.getName().equalsIgnoreCase("vanish")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("staff.vanish.use")) {
                    if (vanishedPlayers.contains(player)) {
                        // Przywróć widoczność dla gracza
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (!p.hasPermission("staff.vanish.see")) {
                                p.showPlayer(this, player);
                            }
                        }
                        vanishedPlayers.remove(player);
                        player.setDisplayName(player.getName()); // Resetuj wyświetlanie
                        player.setPlayerListName(player.getName());
                        // player.performCommand("nte player " +player.getName() +" clear");
                        player.performCommand("tag " +player.getName() +" reset");
                        player.performCommand("god");
                        player.sendTitle(ChatColor.GREEN + "§aYou are here now", ChatColor.YELLOW + "§bvisible", 10, 70, 20);
                        player.sendMessage("§aYou are now visible");
                    } else {
                        // Ukryj gracza przed innymi
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (!p.hasPermission("staff.vanish.see")) {
                                p.hidePlayer(this, player);
                            }
                        }

                        vanishedPlayers.add(player);

                        player.performCommand("god");
                        //player.performCommand("nametagedit player " +player.getName() +" prefix §7[§bV§7] §f");


                            player.performCommand("tag " +player.getName() +" &7[&bV§7] &f  ");


                        player.sendTitle(ChatColor.GREEN + "§aYou are here now", ChatColor.YELLOW + "§binvisible", 10, 70, 20);
                        player.sendMessage("§aYou are invisible now");

                        // Rozpocznij cykliczne wysyłanie wiadomości na ActionBar
                        startActionBarTask(player);
                    }
                } else {
                    player.sendMessage("You do not have permission to use this command.");
                }
                return true;
            } else {
                sender.sendMessage("Only players can use this command.");
                return false;
            }
        }



        if (command.getName().equalsIgnoreCase("gm")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "Usage: /gm <mode>");
                    return true;
                }
                if (player.hasPermission("staff.gm.use")) {
                    String mode = args[0];
                    switch (mode) {
                        case "0":
                            player.setGameMode(GameMode.SURVIVAL);
                            player.sendMessage(ChatColor.GREEN + "You have changed the game mode to §bSURVIVAL");
                            player.sendTitle(ChatColor.GREEN + "You have changed the game mode to", ChatColor.YELLOW + "§bSURVIVAL", 10, 70, 20);
                            break;
                        case "1":
                            player.setGameMode(org.bukkit.GameMode.CREATIVE);
                            player.sendMessage(ChatColor.GREEN + "You have changed the game mode to §bCREATIVE");
                            player.sendTitle(ChatColor.GREEN + "You have changed the game mode to", ChatColor.YELLOW + "§bCREATIVE", 10, 70, 20);
                            break;
                        case "2":
                            player.setGameMode(org.bukkit.GameMode.ADVENTURE);
                            player.sendMessage(ChatColor.GREEN + "You have changed the game mode to §bADVENTURE");
                            player.sendTitle(ChatColor.GREEN + "You have changed the game mode to", ChatColor.YELLOW + "§bADVENTURE", 10, 70, 20);
                            break;
                        case "3":
                            player.setGameMode(org.bukkit.GameMode.SPECTATOR);
                            player.sendMessage(ChatColor.GREEN + "You have changed the game mode to §bSPECTATOR");

                            player.sendTitle(ChatColor.GREEN + "You have changed the game mode to", ChatColor.YELLOW + "§bSPECTATOR", 10, 70, 20);
                            break;
                        default:
                            player.sendMessage(ChatColor.RED + "Invalid mode. Use 1 for Creative, 2 for Survival, 3 for Adventure.");
                            break;
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                }
                return true;
            } else {
                sender.sendMessage("Only players can use this command.");
                return false;
            }
        }
        if (label.equalsIgnoreCase("speed")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("staff.speed.use")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /speed <1-10>");
                player.sendMessage(ChatColor.GREEN + "/speed 2 - Default speed");
                return true;
            }

            try {
                int speed = Integer.parseInt(args[0]);

                if (speed < 1 || speed > 10) {
                    player.sendMessage(ChatColor.RED + "Enter a number from 1 to 10.");
                    return true;
                }

                float speedValue = speed / 10.0f;

                if (player.isFlying()) {
                    player.setFlySpeed(speedValue);
                    player.sendMessage(ChatColor.GREEN + "Flying speed has been set to " + speed + ".");
                } else {
                    player.setWalkSpeed(speedValue);
                    player.sendMessage(ChatColor.GREEN + "Walking speed has been set to  " + speed + ".");
                }

            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Enter a number from 1 to 10.");
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("heal")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("staff.heal.use")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            if (args.length == 0) {
                // Heal the player who executed the command
                player.setHealth(player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());
                player.setFoodLevel(20); // Przywróć głód do maksimum
                player.sendMessage(ChatColor.GREEN + "Your health and hunger have been restored!");
            } else if (args.length == 1) {
                // Heal another player
                Player target = getServer().getPlayer(args[0]);

                if (target == null) {
                    player.sendMessage(ChatColor.RED + "The player with this name is not online.");
                    return true;
                }

                target.setHealth(target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());
                target.setFoodLevel(20); // Przywróć głód do maksimum
                target.sendMessage(ChatColor.GREEN + "Your health and hunger have been restored by " + player.getName() + "!");
                player.sendMessage(ChatColor.GREEN + "Restored player's health and hunger " + target.getName() + "!");
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /heal [player]");
            }

            return true;
        }
        if (command.getName().equalsIgnoreCase("tp")) {
            // Sprawdź, czy sender jest graczem
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;

            // Sprawdź, czy gracz ma uprawnienia
            if (!player.hasPermission("staff.teleport.use")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            // Sprawdź, czy podano gracza docelowego
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /tp <player>");
                return true;
            }

            Player target = getServer().getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "The player with this name is not online.");
                return true;
            }

            // Teleportuj gracza
            player.teleport(target.getLocation());
            player.sendMessage(ChatColor.GREEN + "You teleported to " + target.getName() + ".");
            return true;
        }




        if (command.getName().equalsIgnoreCase("fly")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                // Sprawdzenie uprawnień
                if (!player.hasPermission("staff.fly.use")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }

                // Zmiana trybu latania
                if (player.getAllowFlight()) {
                    player.setFlying(false);
                    player.setAllowFlight(false);

                    player.sendMessage(ChatColor.GREEN + "Flying disabled");
                } else {
                    player.setAllowFlight(true);
                    player.setFlying(true);

                    player.sendMessage(ChatColor.GREEN + "Flying enabled");
                    fly.add(player);
                }

                return true;
            } else {
                sender.sendMessage("Only players can use this command.");
                return false;
            }
        }
        return false;
    }
    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', staffHelpTitle));
        for (String line : staffHelpCommands) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', staffHelpFooter));
    }
    private void startActionBarTask(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {



                if (player.getGameMode() == GameMode.CREATIVE ) {
                    // Dodaj tutaj swoją logikę dla graczy w trybie Creative lub wyższym

                }
                if (player.getGameMode() == GameMode.ADVENTURE ) {
                    // Dodaj tutaj swoją logikę dla graczy w trybie Creative lub wyższym

                }
                if (player.getGameMode() == GameMode.SPECTATOR ) {
                    // Dodaj tutaj swoją logikę dla graczy w trybie Creative lub wyższym

                }if (player.getGameMode() == GameMode.SURVIVAL ) {
                    // Dodaj tutaj swoją logikę dla graczy w trybie Creative lub wyższym

                }


                if (player.getAllowFlight() == true) {

                    String flyon = " §a§lFLY §b§l✔";
                    TextComponent textComponent1 = new TextComponent(flyon);
                    if (vanishedPlayers.contains(player)) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lVanish §b§l✔"),  textComponent1);

                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lVanish §c§l✘"),  textComponent1);

                    }
                }
                else {
                    String flyoff = " §a§lFLY §c§l✘";
                    TextComponent textComponent1 = new TextComponent(flyoff);
                    if (vanishedPlayers.contains(player)) {

                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lVanish §b§l✔"),  textComponent1);

                    }
                    else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lVanish §c§l✘"),  textComponent1);

                    }
                }



            }
        }.runTaskTimer(this, 0, actionBarInterval); // Uruchom zadanie co 20 ticków
    }
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) { // Sprawdzamy, czy napastnikiem jest gracz
            Player attacker = (Player) event.getDamager();
            if (vanishedPlayers.contains(attacker)) { // Sprawdzamy, czy napastnik jest w trybie vanish
                event.setCancelled(true); // Zablokuj atak
            }else {
                event.setCancelled(false);
            }
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // Sprawdź, czy gracz jest w trybie vanish
        if (vanishedPlayers.contains(player)) {
            event.setCancelled(true); // Zablokuj łamanie bloku
        }
        else {
            event.setCancelled(false);
        }
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true); // Zablokuj niszczenie bloków
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        // Sprawdź, czy gracz jest w trybie vanish
        if (vanishedPlayers.contains(player)) {
            event.setCancelled(true); // Zablokuj łamanie bloku
        }
        else {
            event.setCancelled(false);
        }
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true); // Zablokuj niszczenie bloków
        }
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();

        // Ustaw format wiadomości czatu
        String formattedMessage = chatFormat.replace("{player}", playerName).replace("{message}", message);
        event.setFormat(ChatColor.translateAlternateColorCodes('&', formattedMessage));

        UUID playerUUID = event.getPlayer().getUniqueId();
        if (mutedPlayers.containsKey(playerUUID)) {
            Date expirationDate = mutedPlayers.get(playerUUID);
            if (expirationDate == null || expirationDate.after(new Date())) {
                String reason = muteReasons.getOrDefault(playerUUID, "No reason given.");
                event.getPlayer().sendMessage(ChatColor.RED + "You are muted. Reason: " + reason);
                event.setCancelled(true);
            } else {
                mutedPlayers.remove(playerUUID);
                muteReasons.remove(playerUUID);
            }
        }
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (godModePlayers.contains(player.getUniqueId())) {
                event.setCancelled(true); // Anulujemy obrażenia
            }
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true); // Zablokuj ruch
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true); // Zablokuj skakanie
        }
    }



    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && frozenPlayers.contains(event.getEntity())) {
            event.setCancelled(true); // Zablokuj obrażenia
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true); // Zablokuj interakcje
        }
    }
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player targetPlayer = (Player) event.getTarget();
            // Jeśli gracz jest w trybie vanish, resetuj cel moba
            if (vanishedPlayers.contains(targetPlayer)) {
                event.setCancelled(true);
                event.setTarget(null); // Opcjonalnie ustaw cel na null
            }

        }
    }

    private void setPlayerTag(Player player, String tag) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            Scoreboard scoreboard = manager.getMainScoreboard();
            Team team;

            // Sprawdzanie, czy drużyna już istnieje
            if (scoreboard.getTeam(player.getName()) != null) {
                team = scoreboard.getTeam(player.getName());
            } else {
                team = scoreboard.registerNewTeam(player.getName());
            }

            team.setPrefix(tag + ChatColor.WHITE + " "); // Ustawianie prefiksu
            team.addEntry(player.getName()); // Dodawanie gracza do drużyny
        }
    }

    private void resetPlayerTag(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            Scoreboard scoreboard = manager.getMainScoreboard();
            Team team = scoreboard.getTeam(player.getName());

            if (team != null) {
                team.unregister(); // Usuwanie drużyny, co resetuje tag
                player.setDisplayName(player.getName()); // Resetowanie wyświetlanej nazwy gracza
                player.setPlayerListName(player.getName()); // Resetowanie nazwy na liście graczy
            }
        }
    }
    private boolean handleBanCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("staff.ban")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', banNoPermission));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', banUsage));
            return true;
        }

        String playerName = args[0];
        StringBuilder reasonBuilder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }

        String reason = reasonBuilder.toString().trim();
        if (reason.isEmpty()) {
            reason = "No reason given.";
        }

        Player target = Bukkit.getPlayerExact(playerName);
        if (target != null) {
            String banMessageFormatted = banMessage.replace("{reason}", reason);
            target.kickPlayer(ChatColor.translateAlternateColorCodes('&', banMessageFormatted));
        } else {
            String playerOfflineMessage = banPlayerOffline.replace("{player}", playerName);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', playerOfflineMessage));
        }

        Bukkit.getBanList(BanList.Type.NAME).addBan(playerName, reason, null, sender.getName());

        String banSuccessMessage = banSuccess.replace("{player}", playerName).replace("{reason}", reason);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', banSuccessMessage));
        return true;
    }

    private boolean handleUnbanCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("staff.unban")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', unbanNoPermission));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', unbanUsage));
            return true;
        }

        String playerName = args[0];

        if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(playerName)) {
            String notBannedMessage = unbanNotBanned.replace("{player}", playerName);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', notBannedMessage));
            return true;
        }

        Bukkit.getBanList(BanList.Type.NAME).pardon(playerName);

        String unbanSuccessMessage = unbanSuccess.replace("{player}", playerName);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', unbanSuccessMessage));
        return true;
    }
    private boolean handleTpHereCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("staff.tphere.use")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', tphereNoPermission));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', tphereUsage));
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            String notOnlineMessage = tpherePlayerNotOnline.replace("{player}", args[0]);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', notOnlineMessage));
            return true;
        }

        targetPlayer.teleport(player.getLocation());

        String teleportedMessage = tpherePlayerTeleported.replace("{player}", targetPlayer.getName());
        String notifyTeleport = tphereNotifyTeleport.replace("{player}", player.getName());

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', teleportedMessage));
        targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', notifyTeleport));

        return true;
    }
    private boolean handleFreezeCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', freezeOnlyPlayers));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("staff.freeze")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', freezeNoPermission));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', freezeUsage));
            return true;
        }

        Player target = getServer().getPlayer(args[0]);
        if (target == null) {
            String notOnlineMessage = freezePlayerNotOnline.replace("{player}", args[0]);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', notOnlineMessage));
            return true;
        }

        if (frozenPlayers.contains(target)) {
            // Odblokuj gracza
            frozenPlayers.remove(target);
            target.setWalkSpeed(0.2f); // Przywróć normalną szybkość
            target.setAllowFlight(true);

            String unfrozenMessage = freezePlayerUnfrozen.replace("{player}", target.getName());
            String notifyUnfrozen = freezeNotifyUnfrozen.replace("{player}", player.getName());

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', unfrozenMessage));
            target.sendMessage(ChatColor.translateAlternateColorCodes('&', notifyUnfrozen));
        } else {
            // Zablokuj gracza
            frozenPlayers.add(target);
            target.setWalkSpeed(0); // Zablokuj ruch
            target.setAllowFlight(false);

            String frozenMessage = freezePlayerFrozen.replace("{player}", target.getName());
            String notifyFrozen = freezeNotifyFrozen.replace("{player}", player.getName());

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', frozenMessage));
            target.sendMessage(ChatColor.translateAlternateColorCodes('&', notifyFrozen));
        }

        return true;
    }
    private boolean handleTempBanCommand(CommandSender sender, String[] args) {
        // Sprawdzanie uprawnień
        if (!sender.hasPermission("staff.tempban")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', tempbanNoPermission));
            return true;
        }

        // Sprawdzanie poprawności argumentów
        if (args.length < 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', tempbanUsage));
            return true;
        }

        String playerName = args[0]; // Nazwa gracza
        long duration;

        // Konwertowanie czasu na milisekundy
        try {
            duration = parseTime(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', tempbanInvalidTime));
            return true;
        }

        // Tworzenie powodu
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();
        if (reason.isEmpty()) {
            reason = "No reason given.";
        }

        // Szuka gracza na serwerze
        Player target = Bukkit.getPlayerExact(playerName);
        if (target != null) {
            String kickMessage = tempbanKickMessage
                    .replace("{time}", args[1])
                    .replace("{reason}", reason);
            target.kickPlayer(ChatColor.translateAlternateColorCodes('&', kickMessage));
        } else {
            String offlineMessage = tempbanPlayerOffline.replace("{player}", playerName);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', offlineMessage));
        }

        // Dodawanie do listy banów
        Bukkit.getBanList(BanList.Type.NAME).addBan(playerName, reason, new Date(System.currentTimeMillis() + duration), sender.getName());

        // Zaplanowanie odbanowania po określonym czasie
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getBanList(BanList.Type.NAME).pardon(playerName);
                String unbanMessage = tempbanUnbanConfirm
                        .replace("{player}", playerName)
                        .replace("{time}", args[1]);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', unbanMessage));
            }
        }.runTaskLater(this, duration / 50); // Dzielimy przez 50, ponieważ Bukkit używa ticków (1 tick = 50ms)

        // Potwierdzenie bana
        String banMessage = tempbanBanConfirm
                .replace("{player}", playerName)
                .replace("{time}", args[1])
                .replace("{reason}", reason);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', banMessage));

        return true;
    }

    private long parseTime(String time) {
        long value;
        String unit;

        if (time.length() < 2) {
            throw new IllegalArgumentException("Invalid format.");
        }

        // Rozdzielamy liczbę i jednostkę
        value = Long.parseLong(time.substring(0, time.length() - 1));
        unit = time.substring(time.length() - 1).toLowerCase();

        switch (unit) {
            case "s":
                return TimeUnit.SECONDS.toMillis(value);
            case "m":
                return TimeUnit.MINUTES.toMillis(value);
            case "h":
                return TimeUnit.HOURS.toMillis(value);
            case "d":
                return TimeUnit.DAYS.toMillis(value);
            default:
                throw new IllegalArgumentException("Incorrect unit. Use 's', 'm', 'h' or 'd'.");
        }
    }
    private boolean handleMuteCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("staff.mute")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', muteNoPermission));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', muteUsageMute));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mutePlayerNotFound.replace("{player}", args[0])));
            return true;
        }

        String reason = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : defaultMuteReason;
        mutedPlayers.put(target.getUniqueId(), null);
        muteReasons.put(target.getUniqueId(), reason);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', muteSuccess.replace("{player}", target.getName()).replace("{reason}", reason)));
        return true;
    }


    private boolean handleTempMuteCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("staff.tempmute")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', muteNoPermission));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', muteUsageTempMute));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mutePlayerNotFound.replace("{player}", args[0])));
            return true;
        }

        long duration;
        try {
            duration = parseTime(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', incorrectUnit));
            return true;
        }

        Date expirationDate = new Date(System.currentTimeMillis() + duration);
        String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : defaultMuteReason;

        mutedPlayers.put(target.getUniqueId(), expirationDate);
        muteReasons.put(target.getUniqueId(), reason);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedExpiration = dateFormat.format(expirationDate);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', tempMuteSuccess.replace("{player}", target.getName())
                .replace("{duration}", args[1]).replace("{reason}", reason).replace("{expiration}", formattedExpiration)));

        new BukkitRunnable() {
            @Override
            public void run() {
                mutedPlayers.remove(target.getUniqueId());
                muteReasons.remove(target.getUniqueId());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', tempMuteExpired.replace("{player}", target.getName())));
            }
        }.runTaskLater(this, duration / 50);

        return true;
    }

    private boolean handleUnmuteCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("staff.unmute")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', muteNoPermission));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', muteUsageUnmute));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mutePlayerNotFound.replace("{player}", args[0])));
            return true;
        }

        if (mutedPlayers.containsKey(target.getUniqueId())) {
            mutedPlayers.remove(target.getUniqueId());
            muteReasons.remove(target.getUniqueId());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', unmuteSuccess.replace("{player}", target.getName())));
            target.sendMessage(ChatColor.translateAlternateColorCodes('&', unmuteSuccess.replace("{player}", target.getName())));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', notMuted.replace("{player}", target.getName())));
        }

        return true;
    }


}


