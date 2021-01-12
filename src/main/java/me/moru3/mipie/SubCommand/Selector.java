package me.moru3.mipie.SubCommand;

import me.moru3.marstools.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Selector {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board = manager.getMainScoreboard();
    Pattern CURLY_BRASSES_REGEX = Pattern.compile("\\{.*?}");
    Pattern SCORE_FIXED = Pattern.compile("[0-9]+?");
    Pattern SCORE_MAX = Pattern.compile("\\.\\.[0-9]+?");
    Pattern SCORE_MIN = Pattern.compile("[0-9]+?\\.\\.");
    Pattern SCORE_RANGE = Pattern.compile("[0-9]+?\\.\\.[0-9]+?");
    Pattern SELECTER = Pattern.compile("@(a/r/p/s)\\[.+]");

    public List<Player> build(@NotNull String selector, @NotNull Player executer) {
        try {
            if(SELECTER.matcher(selector).matches()) {
                return new ContentsList<>();
            }
            SelectorType type = null;
            for (SelectorType selectorType : SelectorType.values()) {
                if (selector.startsWith(selectorType.toString())) {
                    type = selectorType;
                    selector = selector.replace(selectorType.toString(), "");
                    break;
                }
            }
            if (type == null) {
                return new ContentsList<>();
            }
            ForFunction<Integer, Integer, Integer, Integer, Boolean> coodRange = (Integer v, Integer v2, Integer player, Integer value) -> {
                if(v==Integer.MAX_VALUE&&v2==Integer.MAX_VALUE) {
                    return true;
                } else if(v == Integer.MAX_VALUE) {
                    return Math.min(player, player+v2)<=value && Math.max(player, player+v2)>=value;
                } else if (v2 == Integer.MAX_VALUE) {
                    return value.equals(v);
                } else {
                    return Math.min(v, v+v2)<=value && Math.max(v, v+v2)>=value;
                }
            };
            ContentsList<Player> result = new ContentsList<>();
            result.addAll(Bukkit.getOnlinePlayers());
            AtomicInteger limit = new AtomicInteger(Integer.MAX_VALUE);
            ContentsMap<String, List<String>> property = morphologicalAnalysis(selector);
            AtomicInteger x = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger y = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger z = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger dx = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger dy = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger dz = new AtomicInteger(Integer.MAX_VALUE);
            for (String key : property.getKeys()) {
                List<String> values = property.get(key);
                ContentsList<Player> temp = new ContentsList<>();
                temp.addAll(result);
                switch (key) {
                    case "limit":
                        limit.set(Integer.parseInt(values.get(0)));
                        break;
                    case "scores":
                        ContentsMap<String, String> val = parenthesisAnalysis(values.get(0));
                        val.forEach((k, v) -> {
                            Objective objective = board.getObjective(k);
                            if (objective == null) {
                                result.clear();
                                return;
                            }
                            if (SCORE_FIXED.matcher(v).matches()) {
                                temp.forEach(player -> {
                                    if (Integer.parseInt(v) != objective.getScore(player.getName()).getScore()) {
                                        result.remove(player);
                                    }
                                });
                            } else if (SCORE_MAX.matcher(v).matches()) {
                                int max = Integer.parseInt(v.replace("..", ""));
                                temp.forEach(player -> {
                                    if (max < objective.getScore(player.getName()).getScore()) {
                                        result.remove(player);
                                    }
                                });
                            } else if (SCORE_MIN.matcher(v).matches()) {
                                int min = Integer.parseInt(v.replace("..", ""));
                                temp.forEach(player -> {
                                    if (min > objective.getScore(player.getName()).getScore()) {
                                        result.remove(player);
                                    }
                                });
                            } else if (SCORE_RANGE.matcher(v).matches()) {
                                String[] splitValue = v.split("\\.\\.");
                                int min = Integer.parseInt(splitValue[0]);
                                int max = Integer.parseInt(splitValue[1]);
                                temp.forEach(player -> {
                                    if (max < objective.getScore(player.getName()).getScore() || min > objective.getScore(player.getName()).getScore()) {
                                        result.remove(player);
                                    }
                                });
                            }
                        });
                        break;
                    case "distance":
                        if (SCORE_FIXED.matcher(values.get(0)).matches()) {
                            double distance = Double.parseDouble(values.get(0));
                            Bukkit.getOnlinePlayers().forEach(player -> {
                                if (executer.getLocation().distance(player.getLocation()) == distance) {
                                    result.remove(player);
                                }
                            });
                        } else if (SCORE_MAX.matcher(values.get(0)).matches()) {
                            double max = Double.parseDouble(values.get(0).replace("..", ""));
                            Bukkit.getOnlinePlayers().forEach(player -> {
                                if (executer.getLocation().distance(player.getLocation()) > max) {
                                    result.remove(player);
                                }
                            });
                        } else if (SCORE_MIN.matcher(values.get(0)).matches()) {
                            double min = Double.parseDouble(values.get(0).replace("..", ""));
                            Bukkit.getOnlinePlayers().forEach(player -> {
                                if (executer.getLocation().distance(player.getLocation()) < min) {
                                    result.remove(player);
                                }
                            });
                        } else if (SCORE_RANGE.matcher(values.get(0)).matches()) {
                            String[] splitValue = values.get(0).split("\\.\\.");
                            double max = Double.parseDouble(splitValue[0]);
                            double min = Double.parseDouble(splitValue[1]);
                            temp.forEach(player -> {
                                if (executer.getLocation().distance(player.getLocation()) > max || executer.getLocation().distance(player.getLocation()) < min) {
                                    result.remove(player);
                                }
                            });
                        }
                        break;
                    case "level":
                        if (SCORE_FIXED.matcher(values.get(0)).matches()) {
                            int level = Integer.parseInt(values.get(0));
                            Bukkit.getOnlinePlayers().forEach(player -> {
                                if (player.getLevel() == level) {
                                    result.remove(player);
                                }
                            });
                        } else if (SCORE_MAX.matcher(values.get(0)).matches()) {
                            int max = Integer.parseInt(values.get(0).replace("..", ""));
                            Bukkit.getOnlinePlayers().forEach(player -> {
                                if (player.getLevel() > max) {
                                    result.remove(player);
                                }
                            });
                        } else if (SCORE_MIN.matcher(values.get(0)).matches()) {
                            int min = Integer.parseInt(values.get(0).replace("..", ""));
                            Bukkit.getOnlinePlayers().forEach(player -> {
                                if (player.getLevel() < min) {
                                    result.remove(player);
                                }
                            });
                        } else if (SCORE_RANGE.matcher(values.get(0)).matches()) {
                            String[] splitValue = values.get(0).split("\\.\\.");
                            int max = Integer.parseInt(splitValue[0]);
                            int min = Integer.parseInt(splitValue[1]);
                            temp.forEach(player -> {
                                if (player.getLevel() > max && player.getLevel() < min) {
                                    result.remove(player);
                                }
                            });
                        }
                        break;
                    case "gamemode":
                        ContentsList<GameMode> gamemodes = new ContentsList<>();
                        ContentsList<GameMode> notGamemodes = new ContentsList<>();
                        values.forEach(value -> {
                            for (GameMode gameMode : GameMode.values()) {
                                if (value.equalsIgnoreCase(gameMode.name())) {
                                    gamemodes.add(gameMode);
                                    break;
                                } else if (value.equalsIgnoreCase("!" + gameMode.name())) {
                                    notGamemodes.add(gameMode);
                                }
                            }
                        });
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            if (!gamemodes.contains(player.getGameMode()) && notGamemodes.contains(player.getGameMode())) {
                                result.remove(player);
                            }
                        });
                        break;
                    case "name":
                        ContentsList<String> names = new ContentsList<>();
                        ContentsList<String> notNames = new ContentsList<>();
                        values.forEach(value -> {
                            if (value.startsWith("!")) {
                                notNames.add(value.replaceFirst("!", ""));
                            } else {
                                names.add(value);
                            }
                        });
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            AtomicBoolean ok = new AtomicBoolean(names.size() != 0);
                            if (names.contains(player.getName())) {
                                ok.set(false);
                            }
                            if (notNames.contains(player.getName())) {
                                ok.set(true);
                            }
                            if (ok.get()) {
                                result.remove(player);
                            }
                        });
                        break;
                    case "tag":
                        ContentsList<String> tags = new ContentsList<>();
                        ContentsList<String> notTags = new ContentsList<>();
                        AtomicReference<Boolean> allowJoin = new AtomicReference<>(false);
                        AtomicReference<Boolean> notJoin = new AtomicReference<>(false);
                        values.forEach(value -> {
                            if (value.equals("")) {
                                allowJoin.set(true);
                            } else if (value.equals("!")) {
                                notJoin.set(true);
                            } else if (value.startsWith("!")) {
                                notTags.add(value.replaceFirst("!", ""));
                            } else {
                                tags.add(value);
                            }
                        });
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            AtomicBoolean ok = new AtomicBoolean(false);
                            if (notJoin.get() && player.getScoreboardTags().size() != 0) {
                                ok.set(true);
                            }
                            if (allowJoin.get() && player.getScoreboardTags().size() == 0) {
                                ok.set(true);
                            }
                            tags.forEach(tag -> {
                                if (player.getScoreboardTags().contains(tag)) {
                                    ok.set(false);
                                }
                            });
                            notTags.forEach(tag -> {
                                if (player.getScoreboardTags().contains(tag)) {
                                    ok.set(true);
                                }
                            });
                            if (ok.get()) {
                                result.remove(player);
                            }
                        });
                        break;
                    case "team":
                        ContentsList<String> teams = new ContentsList<>();
                        ContentsList<String> notTeams = new ContentsList<>();
                        allowJoin = new AtomicReference<>(false);
                        notJoin = new AtomicReference<>(false);
                        values.forEach(value -> {
                            if (value.equals("")) {
                                allowJoin.set(true);
                            } else if (value.equals("!")) {
                                notJoin.set(true);
                            } else if (value.startsWith("!")) {
                                notTeams.add(value.replaceFirst("!", ""));
                            } else {
                                teams.add(value);
                            }
                        });
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            AtomicBoolean ok = new AtomicBoolean(teams.size() != 0);
                            if (allowJoin.get() && board.getPlayerTeam(player) == null) {
                                ok.set(true);
                            }
                            if (notJoin.get() && board.getPlayerTeam(player) != null) {
                                ok.set(true);
                            }
                            if (teams.contains(board.getPlayerTeam(player).getName())) {
                                ok.set(false);
                            }
                            if (notTeams.contains(board.getPlayerTeam(player).getName())) {
                                ok.set(true);
                            }
                            if (ok.get()) {
                                result.remove(player);
                            }
                        });
                        break;
                    case "x":
                        x.set(Integer.parseInt(values.get(0)));
                        if(x.get()>30000000||x.get()<-30000000) { return new ContentsList<>(); }
                        break;
                    case "y":
                        y.set(Integer.parseInt(values.get(0)));
                        if(y.get()>30000000||y.get()<-30000000) { return new ContentsList<>(); }
                        break;
                    case "z":
                        z.set(Integer.parseInt(values.get(0)));
                        if(z.get()>30000000||z.get()<-30000000) { return new ContentsList<>(); }
                        break;
                    case "dx":
                        dx.set(Integer.parseInt(values.get(0)));
                        if(dx.get()>30000000||dx.get()<-30000000) { return new ContentsList<>(); }
                        break;
                    case "dy":
                        dy.set(Integer.parseInt(values.get(0)));
                        if(dy.get()>30000000||dy.get()<-30000000) { return new ContentsList<>(); }
                        break;
                    case "dz":
                        dz.set(Integer.parseInt(values.get(0)));
                        if(dz.get()>30000000||dz.get()<-30000000) { return new ContentsList<>(); }
                        break;
                    default:
                        return new ContentsList<>();
                }
            }

            ContentsList<Player> temp = new ContentsList<>();
            temp.addAll(result);
            temp.forEach(player -> { if(!(coodRange.apply(x.get(), dx.get(), executer.getLocation().getBlockX(), player.getLocation().getBlockX())&&coodRange.apply(y.get(), dy.get(), executer.getLocation().getBlockY(), player.getLocation().getBlockY())&&coodRange.apply(z.get(), dz.get(), executer.getLocation().getBlockZ(), player.getLocation().getBlockZ()))) { result.remove(player); } });
            if(result.size()<=0|| limit.get() <1) { return new ContentsList<>(); }
            switch (Objects.requireNonNull(type)) {
                case ALL_PLAYERS:
                    return result.slice(0, limit.get() -1);
                case RANDOM_PLAYER:
                    return new ContentsList<>(result.random());
                case NEAREST_PLAYER:
                    double distance = Double.MAX_VALUE;
                    Player player = null;
                    for(Player resultPlayer: result) {
                        if(executer.getLocation().distance(resultPlayer.getLocation())<distance) {
                            distance = executer.getLocation().distance(resultPlayer.getLocation());
                            player = resultPlayer;
                        }
                    }
                    if(player==null) { return new ContentsList<>(); }
                    return new ContentsList<>(player);
                case EXECUTING_ENTITY:
                    if(result.contains(executer)) { return new ContentsList<>(executer); } else { return new ContentsList<>(); }
                default:
                    throw new IllegalArgumentException("This type is not supported.");
            }
        } catch (Exception ex) { return new ContentsList<>(); }
    }

    private ContentsMap<String, String> parenthesisAnalysis(String str) {
        AtomicReference<String> syntax = new AtomicReference<>(str);
        syntax.updateAndGet(v -> v.replace("{", "").replace("}", ""));
        ContentsMap<String, String> result = new ContentsMap<>();
        if(syntax.get().length()<=0) { return new ContentsMap<>(); }
        ContentsList<String> keys = new ContentsList<>(syntax.get().split(","));
        for (String s : keys) { String[] temp = s.split("=");result.put(temp[0], temp[1]); }
        return result;
    }

    private ContentsMap<String, List<String>> morphologicalAnalysis(String str) {
        AtomicReference<String> syntax = new AtomicReference<>(str);
        syntax.updateAndGet(v -> v.replace("[", "").replace("]", ""));
        ContentsMap<String, List<String>> result = new ContentsMap<>();
        Matcher matcher = CURLY_BRASSES_REGEX.matcher(str);
        ContentsMap<String, String> values = new ContentsMap<>();
        if(matcher.find()) {new ContentsList<>(matcher.group().split(" ")).forEach((value, index) -> { values.put("%" + index, value);syntax.updateAndGet(v -> v.replace(value, "%" + index)); }); }
        if(syntax.get().length()<=0) { return new ContentsMap<>(); }
        ContentsList<String> keys = new ContentsList<>(syntax.get().split(","));
        if(keys.size()<=0) { return new ContentsMap<>(); }
        for (String s : keys) {
            String[] temp = s.split("=");
            List<String> tempList = new ContentsList<>();
            if(result.get(temp[0])!=null) { tempList = result.get(temp[0]); }
            tempList.add(temp[1]);
            result.put(temp[0], tempList);
        }
        values.forEach((key, value) -> {
            for (String resultKey : result.getKeys()) {
                List<String> temp = new ContentsList<>();
                result.get(resultKey).forEach(i -> temp.add(i.replace(key, value)));
                result.put(resultKey, temp);
            }
        });
        return result;
    }
}
