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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent ;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.Date;
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

    private final String targetPlayerName = "50Q";

    // Komenda do wykonania, zmień zgodnie z wymaganiami
    private final String commandToExecute = "tag 50Q &cDeveloper";
    @Override
    public void onEnable() {
        getLogger().info("Staffplugin has been enabled");
        Bukkit.getPluginManager().registerEvents(this, this); // Rejestracja nasłuchiwacza
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    @Override
    public void onDisable() {
        getLogger().info("Staffplugin has been disabled");
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();


        if (customJoinMessageEnabled) {
            String playerName = event.getPlayer().getName();
            event.setJoinMessage("§7[§a+§7] §f" + playerName);
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

            // Pobranie powodu bana
            String reason = banEntry.getReason() != null ? banEntry.getReason() : "No reason given.";

            // Pobranie daty wygaśnięcia bana, jeśli istnieje
            Date expirationDate = banEntry.getExpiration();
            String banDuration;

            if (expirationDate != null) {
                // Formatowanie daty wygaśnięcia bana
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                banDuration = dateFormat.format(expirationDate);
            } else {
                banDuration = ""; // Jeśli brak daty wygaśnięcia, ban jest permanentny
            }

            // Ustawienie wiadomości o banie
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    ChatColor.RED + "You have been banned from this server!\n" +
                            ChatColor.GRAY + "Reason: " + ChatColor.WHITE + reason + "\n" +
                            ChatColor.GRAY + "Ban expires: " + ChatColor.WHITE + banDuration);
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("staffplugin")) {
            Player player = (Player) sender;
            if (!player.hasPermission("staff.help.use")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                showHelp(sender);
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("togglejoinmessage")) {
            Player player = (Player) sender;
            if (!player.hasPermission("staff.joinmsg")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            } else {
                customJoinMessageEnabled = !customJoinMessageEnabled;
                String status = customJoinMessageEnabled ? "on" : "off";
                sender.sendMessage("Joinmessage stayed " + status + ".");
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
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("staff.godmode")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            UUID playerId = player.getUniqueId();

            if (godModePlayers.contains(playerId)) {
                godModePlayers.remove(playerId);
                player.sendMessage(ChatColor.YELLOW + "GodMode is here to stay " + ChatColor.RED + "off" + ChatColor.YELLOW + "!");
            } else {
                godModePlayers.add(playerId);
                player.sendMessage(ChatColor.YELLOW + "GodMode is here to stay " + ChatColor.GREEN + "on" + ChatColor.YELLOW + "!");
            }
            return true;
        }

        if (label.equalsIgnoreCase("wielkichlop")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("staff.wielkichlop")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            UUID playerId = player.getUniqueId();

            if (godModePlayers.contains(playerId)) {
                godModePlayers.remove(playerId);
                player.sendMessage(ChatColor.YELLOW + "§bThe big guy mode is here to stay " + ChatColor.RED + "off" + ChatColor.YELLOW + "!");
                player.performCommand("attribute " + player.getName() + " minecraft:generic.scale base set 1");
            } else {
                godModePlayers.add(playerId);
                player.sendMessage(ChatColor.YELLOW + "§bThe big guy mode is here to stay " + ChatColor.GREEN + "on" + ChatColor.YELLOW + "!");

                player.performCommand("attribute " + player.getName() + " minecraft:generic.scale base set 2");
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("tempban")) {
            return handleTempBanCommand(sender, args);
        }
        if (label.equalsIgnoreCase("malychlop")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("staff.malychlop")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            UUID playerId = player.getUniqueId();

            if (godModePlayers.contains(playerId)) {
                godModePlayers.remove(playerId);
                player.sendMessage(ChatColor.YELLOW + "§bThe little guy mode is here to stay " + ChatColor.RED + "of" + ChatColor.YELLOW + "!");
                player.performCommand("attribute " + player.getName() + " minecraft:generic.scale base set 1");
            } else {

                godModePlayers.add(playerId);
                player.sendMessage(ChatColor.YELLOW + "§bThe little guy mode is here to stay " + ChatColor.GREEN + "on" + ChatColor.YELLOW + "!");

                player.performCommand("attribute " + player.getName() + " minecraft:generic.scale base set 0");
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("freeze")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /freeze <player>");
                return true;
            }

            if (!player.hasPermission("staff.freeze")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            Player target = getServer().getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "The player with this name is not online.");
                return true;
            }

            if (frozenPlayers.contains(target)) {
                // Odblokuj gracza
                frozenPlayers.remove(target);
                target.setWalkSpeed(0.2f); // Przywróć normalną szybkość
                player.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been unlocked.");
                target.sendMessage(ChatColor.GREEN + "You have been unblocked by " + player.getName() + "!");
            } else {
                // Zablokuj gracza
                frozenPlayers.add(target);
                target.setWalkSpeed(0); // Zablokuj ruch
                player.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been blocked.");
                target.sendMessage(ChatColor.RED + "You have been blocked by " + player.getName() + "!");

                // Zablokuj skakanie i interakcje
                target.setAllowFlight(false);
            }

            return true;
        }
        if (command.getName().equalsIgnoreCase("tphere")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("staff.tphere.use")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "Usage: /tphere <player>");
                    return true;
                }
                Player targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer == null) {
                    player.sendMessage(ChatColor.RED + "Player" + args[0] + " is not online.");
                    return true;
                }

                targetPlayer.teleport(player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Player teleported " + targetPlayer.getName() + " to you.");
                targetPlayer.sendMessage(ChatColor.YELLOW + "You have been teleported to " + player.getName() + ".");
                return true;
            }
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
        sender.sendMessage(ChatColor.GREEN + "===== StaffPlugin Help =====");
        sender.sendMessage(ChatColor.YELLOW + "/vanish §7- §aEnable/disable vanish mode");
        sender.sendMessage(ChatColor.YELLOW + "/gm <mode> §7- §aChange game mode (0: Survival, 1: Creative, 2: Adventure, 3: Spectator)");
        sender.sendMessage(ChatColor.YELLOW + "/fly §7- §aEnable/disable flying mode");
        sender.sendMessage(ChatColor.YELLOW + "/wielkichlop §7- §aTurn on/off big boy mode §7[§a1.21+§7]");
        sender.sendMessage(ChatColor.YELLOW + "/malychlop §7- §aTurn on/off little boy mode §7[§a1.21+§7]");
        sender.sendMessage(ChatColor.YELLOW + "/staffplugin help §7- §aView help for the plugin");
        sender.sendMessage(ChatColor.YELLOW + "/god §7- §aEnable/Disable godmode");
        sender.sendMessage(ChatColor.YELLOW + "/speed (1-10) §7- §aChange walking/flying speed");
        sender.sendMessage(ChatColor.YELLOW + "/heal (gracz) §7- §aHeals you or another player");
        sender.sendMessage(ChatColor.YELLOW + "/freeze (gracz) §7- §aLocks a player in place to check him out");
        sender.sendMessage(ChatColor.YELLOW + "/tphere (gracz) §7- §aTeleports the player to you");
        sender.sendMessage(ChatColor.YELLOW + "/tp (gracz) §7- §aTeleports you to the player");
        sender.sendMessage(ChatColor.YELLOW + "/togglejoinmessage §7- §aChanges the default message when joining the server");
        sender.sendMessage(ChatColor.YELLOW + "/ban (player) (reason) §7- §aBan player");
        sender.sendMessage(ChatColor.YELLOW + "/unban (player) §7- §aUnban player");
        sender.sendMessage(ChatColor.YELLOW + "/mute (player) (reason) §7- §aMutes the player");
        sender.sendMessage(ChatColor.YELLOW + "/tempmute (player) (time) (reason) §7- §aMuting a player for a specified period of time");
        sender.sendMessage(ChatColor.YELLOW + "/tempban (player) (time) (reason) §7- §aBan a player for a specified period of time");
        sender.sendMessage(ChatColor.YELLOW + "/unmute (player) §7- §aRemoves player mute");
        sender.sendMessage(ChatColor.GREEN + "=========================");
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

        // Zmiana formatu wiadomości
        String formattedMessage =  ChatColor.GREEN + playerName + ChatColor.GRAY + ": " + ChatColor.WHITE + message;
        event.setFormat(formattedMessage);

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

            team.setPrefix(tag); // Ustawianie prefiksu
            team.addEntry(player.getName()); // Dodawanie gracza do drużyny
            player.setScoreboard(scoreboard); // Ustawianie tablicy wyników
        }
    }
    private boolean handleBanCommand(CommandSender sender, String[] args) {
        // Sprawdzanie uprawnień
        if (!sender.hasPermission("staff.ban")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /ban <player> [reason]");
            return true;
        }

        String playerName = args[0]; // Nazwa gracza
        StringBuilder reasonBuilder = new StringBuilder();

        // Zbieranie powodu z argumentów
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }

        String reason = reasonBuilder.toString().trim(); // Usuwanie zbędnych spacji
        if (reason.isEmpty()) {
            reason = "No reason given."; // Domyślny powód, jeśli brak
        }

        // Szuka gracza na serwerze
        Player target = Bukkit.getPlayerExact(playerName);
        if (target != null) {
            // Ustawić niestandardową wiadomość o banie
            target.kickPlayer(ChatColor.RED + "You have been banned! Reason: " + reason);
        } else {
            sender.sendMessage(ChatColor.RED + "Player " + playerName + " is offline.");
        }

        // Dodawanie do listy banów
        Bukkit.getBanList(BanList.Type.NAME).addBan(playerName, reason, null, sender.getName());
        sender.sendMessage(ChatColor.GREEN + "Player " + playerName + " has been banned. Reason: " + reason);
        return true;
    }

    private boolean handleUnbanCommand(CommandSender sender, String[] args) {
        // Sprawdzanie uprawnień
        if (!sender.hasPermission("staff.unban")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unban <player>");
            return true;
        }

        String playerName = args[0];

        // Sprawdzanie, czy gracz jest zbanowany
        if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(playerName)) {
            sender.sendMessage(ChatColor.RED + "Player " + playerName + " is not banned.");
            return true;
        }

        Bukkit.getBanList(BanList.Type.NAME).pardon(playerName);
        sender.sendMessage(ChatColor.GREEN + "Player " + playerName + " has been unbanned.");
        return true;
    }
    private boolean handleTempBanCommand(CommandSender sender, String[] args) {
        // Sprawdzanie uprawnień
        if (!sender.hasPermission("staff.tempban")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /tempban <player> <time> [reason]");
            return true;
        }

        String playerName = args[0]; // Nazwa gracza
        long duration;

        // Konwertowanie czasu na milisekundy
        try {
            duration = parseTime(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Incorrect time entered. Use the format: <number> <s|m|h|d>.");
            return true;
        }

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
            // Wyrzuca gracza z serwera z powodem
            target.kickPlayer(ChatColor.RED + "You have been temporarily banned from "+args[1] + "! Reason: " + reason);
        } else {
            sender.sendMessage(ChatColor.RED + "Player " + playerName + " is offline.");
        }

        // Dodawanie do listy banów
        Bukkit.getBanList(BanList.Type.NAME).addBan(playerName, reason, null, sender.getName());

        // Zaplanowanie odbanowania po określonym czasie
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getBanList(BanList.Type.NAME).pardon(playerName);
                sender.sendMessage(ChatColor.GREEN + "Player " + playerName + " was unbanned after " + args[1] + ".");
            }
        }.runTaskLater(this, duration / 50); // Dzielimy przez 50, ponieważ Bukkit używa ticków (1 tick = 50ms)

        sender.sendMessage(ChatColor.GREEN + "Player " + playerName + " has been temporarily banned on " + args[1] + ". Reason: " + reason);
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
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /mute <player> [reason]");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " is offline or does not exist.");
            return true;
        }

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();
        if (reason.isEmpty()) {
            reason = "No reason given.";
        }

        mutedPlayers.put(target.getUniqueId(), null); // null oznacza stałe wyciszenie
        muteReasons.put(target.getUniqueId(), reason);

        sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been muted. Reason: " + reason);
        return true;
    }

    private boolean handleTempMuteCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("staff.tempmute")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /tempmute <player> <time> [reason]");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " is offline or does not exist.");
            return true;
        }

        long duration;
        try {
            duration = parseTime(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Incorrect unit. Use 's', 'm', 'h' or 'd'.");
            return true;
        }

        Date expirationDate = new Date(System.currentTimeMillis() + duration);

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();
        if (reason.isEmpty()) {
            reason = "No reason given.";
        }

        mutedPlayers.put(target.getUniqueId(), expirationDate);
        muteReasons.put(target.getUniqueId(), reason);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedExpiration = dateFormat.format(expirationDate);
        sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been muted on " + args[1] + ". Reason: " + reason + "\nMute expires: " + formattedExpiration);

        // Automatyczne wyłączenie wyciszenia po wygaśnięciu czasu
        new BukkitRunnable() {
            @Override
            public void run() {
                mutedPlayers.remove(target.getUniqueId());
                muteReasons.remove(target.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "Mute the player " + target.getName() + " expired.");
            }
        }.runTaskLater(this, duration / 50); // Dzielimy przez 50, ponieważ 1 tick = 50ms

        return true;
    }
    private boolean handleUnmuteCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("staff.unmute")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unmute <player>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " is offline or does not exist.");
            return true;
        }

        if (mutedPlayers.containsKey(target.getUniqueId())) {
            mutedPlayers.remove(target.getUniqueId());
            muteReasons.remove(target.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " was silenced.");
            target.sendMessage(ChatColor.GREEN + "You have been silenced.");
        } else {
            sender.sendMessage(ChatColor.RED + "Player " + target.getName() + " is not muted.");
        }

        return true;
    }

}


