package com.example.data.model

import androidx.compose.ui.graphics.Color

// Colors representing Beijing Subway Lines
object MetroColors {
    val Line1 = Color(0xFFA42422) // Red
    val Line2 = Color(0xFF005691) // Dark Blue
    val Line4 = Color(0xFF007E7A) // Teal
    val Line5 = Color(0xFF7F1070) // Magenta/Purple
    val Line10 = Color(0xFF0096D1) // Sky Blue
    val AirportExpress = Color(0xFF904F95) // Purple
    val DaxingExpress = Color(0xFF1E3A8A) // Dark Navy
}

data class Station(
    val id: String,
    val nameZh: String,
    val nameEn: String,
    val latitude: Double,
    val longitude: Double,
    val lines: List<String>,
    val exits: List<String> = emptyList(),
    val hasElevator: Boolean = true,
    val hasAccessibleToilet: Boolean = true,
    val amenities: List<String> = emptyList(), // "Restroom", "ATM", "Convenience Store", "Coffee Shop"
    val busTransfers: List<String> = emptyList(),
    val attractions: List<String> = emptyList(),
    val congestionIndex: Float = 1.0f // 1.0: Normal, 1.5: Busy, 2.0: Congested
)

data class Line(
    val id: String,
    val nameZh: String,
    val nameEn: String,
    val color: Color,
    val colorHex: String,
    val stations: List<String> // Station IDs
)

data class RouteLeg(
    val fromStation: Station,
    val toStation: Station,
    val lineId: String,
    val numStations: Int,
    val durationMin: Int
)

data class PlanResult(
    val startStation: Station,
    val endStation: Station,
    val routeList: List<Station>,
    val lineChanges: List<String>, // Line IDs used along the way
    val totalTimeMin: Int,
    val ticketPrice: Int,
    val transfersCount: Int,
    val descriptionZh: String,
    val descriptionEn: String,
    val legs: List<RouteLeg>
)

object BeijingMetroData {
    val stations = mapOf(
        // Line 1
        "pingguoyuan" to Station("pingguoyuan", "苹果园", "Pingguoyuan", 39.9261, 116.1776, listOf("Line1"), listOf("A", "D"), true, true, listOf("Restroom", "Convenience Store"), listOf("336", "977"), listOf("Gongyi Park")),
        "gongzhufen" to Station("gongzhufen", "公主坟", "Gongzhufen", 39.9073, 116.3059, listOf("Line1", "Line10"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM", "Bakery"), listOf("1", "52", "76"), listOf("Yuyuantan Park")),
        "fuxingmen" to Station("fuxingmen", "复兴门", "Fuxingmen", 39.9072, 116.3564, listOf("Line1", "Line2"), listOf("A", "B", "C", "D"), true, false, listOf("ATM"), listOf("44", "84"), listOf("Financial Street")),
        "xidan" to Station("xidan", "西单", "Xidan", 39.9073, 116.3732, listOf("Line1", "Line4"), listOf("A", "B", "C", "D", "E", "F"), true, true, listOf("Restroom", "ATM", "Vending Machine"), listOf("22", "88", "102"), listOf("Xidan Joy City", "Taikang")),
        "tiananmen_west" to Station("tiananmen_west", "天安门西", "Tian'anmen West", 39.9074, 116.3857, listOf("Line1"), listOf("A", "B", "C"), false, false, emptyList(), listOf("1", "5"), listOf("Tian'anmen Square", "Zhongshan Park", "National Grand Theatre")),
        "tiananmen_east" to Station("tiananmen_east", "天安门东", "Tian'anmen East", 39.9075, 116.3976, listOf("Line1"), listOf("A", "B", "C", "D"), false, false, listOf("Restroom"), listOf("1", "2", "120"), listOf("The Palace Museum (Forbidden City)", "National Museum of China")),
        "dongdan" to Station("dongdan", "东单", "Dongdan", 39.9076, 116.4111, listOf("Line1", "Line5"), listOf("A", "B", "C", "D", "E", "F", "G", "H"), true, true, listOf("Restroom", "Convenience Store"), listOf("39", "106", "116"), listOf("Dongdan Park", "Oriental Plaza")),
        "guomao" to Station("guomao", "国贸", "Guomao", 39.9078, 116.4533, listOf("Line1", "Line10"), listOf("A", "B", "C", "D", "E", "F", "G"), true, true, listOf("Restroom", "ATM", "Convenience Store", "Coffee Shop"), listOf("1", "300", "57"), listOf("China World Trade Center", "CCTV Headquarters")),
        "sihui" to Station("sihui", "四惠", "Sihui", 39.9088, 116.4864, listOf("Line1"), listOf("A", "B", "C"), true, true, listOf("Restroom", "Convenience Store"), listOf("322", "405", "475"), listOf("Sihui Transit Hub")),
        "universal_resort" to Station("universal_resort", "环球度假区", "Universal Resort", 39.8491, 116.6713, listOf("Line1"), listOf("A", "B", "C", "D", "E", "F"), true, true, listOf("Restroom", "Family Room", "ATM", "Coffee Shop", "Gift Shop"), listOf("589", "T116"), listOf("Universal Studios Beijing")),

        // Line 2
        "xizhimen" to Station("xizhimen", "西直门", "Xizhimen", 39.9405, 116.3486, listOf("Line2", "Line4"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM", "Convenience Store"), listOf("16", "26", "87"), listOf("Beijing Zoo", "Xihuan Plaza")),
        "guloudajie" to Station("guloudajie", "鼓楼大街", "Guloudajie", 39.9482, 116.3862, listOf("Line2"), listOf("A", "B", "G", "H"), true, false, listOf("Restroom"), listOf("5", "60", "82"), listOf("Bell and Drum Towers", "Shichahai Lake")),
        "yonghegong" to Station("yonghegong", "雍和宫", "Yonghegong Lama Temple", 39.9479, 116.4109, listOf("Line2", "Line5"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM"), listOf("13", "116", "117"), listOf("Yonghe Temple", "Guozijian (Imperial College)", "Ditan Park")),
        "dongzhimen" to Station("dongzhimen", "东直门", "Dongzhimen", 39.9407, 116.4272, listOf("Line2", "AirportExpress"), listOf("A", "B", "C", "D", "E"), true, true, listOf("Restroom", "ATM", "Coffee Shop", "Convenience Store"), listOf("916", "980", "3"), listOf("Raffles City", "Sanlitun Bar Street")),
        "chaoyangmen" to Station("chaoyangmen", "朝阳门", "Chaoyangmen", 39.9244, 116.4269, listOf("Line2"), listOf("A", "B", "G", "H"), true, true, listOf("Restroom"), listOf("44", "110", "112"), listOf("Galaxy SOHO", "Ministry of Foreign Affairs")),
        "jianguomen" to Station("jianguomen", "建国门", "Jianguomen", 39.9077, 116.4273, listOf("Line1", "Line2"), listOf("A", "B", "C", "D"), true, false, listOf("ATM"), listOf("1", "52", "120"), listOf("Ancient Observatory")),
        "beijing_railway" to Station("beijing_railway", "北京站", "Beijing Railway Station", 39.9012, 116.4206, listOf("Line2"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM", "Convenience Store", "Fast Food"), listOf("9", "24", "126"), listOf("Beijing Railway Station", "Ming City Wall Ruins Park")),
        "qianmen" to Station("qianmen", "前门", "Qianmen", 39.8996, 116.3916, listOf("Line2"), listOf("A", "B", "C"), true, true, listOf("Restroom", "ATM"), listOf("8", "22", "82"), listOf("Qianmen Street", "Chairman Mao Memorial Hall", "Tian'anmen Square")),
        "xuanwumen" to Station("xuanwumen", "宣武门", "Xuanwumen", 39.8995, 116.3667, listOf("Line2", "Line4"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM"), listOf("102", "105"), listOf("Sogo", "Cathedral of the Immaculate Conception")),

        // Line 4
        "anheqiao_north" to Station("anheqiao_north", "安河桥北", "Anheqiao North", 40.0075, 116.2625, listOf("Line4"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom"), listOf("512", "575"), listOf("Anheqiao")),
        "zhongguancun" to Station("zhongguancun", "中关村", "Zhongguancun", 39.9806, 116.3101, listOf("Line4"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM", "Coffee Shop"), listOf("302", "307", "400"), listOf("Zhongguancun Science Park", "Peking University")),
        "haidianhuangzhuang" to Station("haidianhuangzhuang", "海淀黄庄", "Haidianhuangzhuang", 39.9733, 116.3102, listOf("Line4", "Line10"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM", "Convenience Store"), listOf("302", "305", "320"), listOf("Haidian Theatre", "Dangdai Plaza")),
        "national_library" to Station("national_library", "国家图书馆", "National Library", 39.9404, 116.3188, listOf("Line4"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM", "Bookstore"), listOf("86", "320", "332"), listOf("National Library of China", "Purple Bamboo Park")),
        "pinganli" to Station("pinganli", "平安里", "Pinganli", 39.9288, 116.3639, listOf("Line4"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom"), listOf("3", "13", "22"), listOf("Huguo Xijie", "Prince Gong's Mansion")),
        "beijing_south" to Station("beijing_south", "北京南站", "Beijing South Railway Station", 39.8596, 116.3698, listOf("Line4"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM", "Food Court", "Convenience Store"), listOf("20", "102", "106"), listOf("Beijing South Railway Station", "Temple of Heaven")),
        "jiaomen_west" to Station("jiaomen_west", "Jiaomen West", "Jiaomen West", 39.8427, 116.3696, listOf("Line4", "Line10"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom"), listOf("72", "324"), listOf("Jiaomen Plaza")),
        "tiangongyuan" to Station("tiangongyuan", "天宫院", "Tiangongyuan", 39.6705, 116.3115, listOf("Line4"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM"), listOf("937", "X102"), listOf("Tiangong Mall")),

        // Line 5
        "tiantongyuan_north" to Station("tiantongyuan_north", "天通苑北", "Tiantongyuan North", 40.0699, 116.4069, listOf("Line5"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "Convenience Store"), listOf("快速公交3号线", "530"), listOf("Tiantongyuan Residential Zone")),
        "huixinxijie_nankou" to Station("huixinxijie_nankou", "惠新西街南口", "Huixinxijie Nankou", 39.9723, 116.4113, listOf("Line5", "Line10"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM"), listOf("18", "125", "409"), listOf("China-Japan Friendship Hospital", "UIBE University")),
        "chongwenmen" to Station("chongwenmen", "崇文门", "Chongwenmen", 39.8978, 116.4112, listOf("Line2", "Line5"), listOf("A", "B", "C", "D", "E", "F"), true, true, listOf("Restroom", "ATM", "Bakery"), listOf("8", "39", "41"), listOf("Chongwenmen Glory Mall", "Tongren Hospital")),
        "tiantandongmen" to Station("tiantandongmen", "天坛东门", "Tiantandongmen", 39.8821, 116.4114, listOf("Line5"), listOf("A", "B", "C"), true, true, listOf("Restroom"), listOf("6", "34", "36"), listOf("Temple of Heaven (East Gate)", "Hongqiao Pearl Market")),
        "songjiazhuang" to Station("songjiazhuang", "宋家庄", "Songjiazhuang", 39.8415, 116.4198, listOf("Line5", "Line10"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM", "Vending Machine"), listOf("17", "39", "511"), listOf("Songjiazhuang Transit Hub")),

        // Line 10
        "beitucheng" to Station("beitucheng", "北土城", "Beitucheng", 39.9722, 116.3861, listOf("Line10"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom"), listOf("81", "82", "380"), listOf("Yuan Dynasty Wall Relics Park", "Olympic Green")),
        "sanyuanqiao" to Station("sanyuanqiao", "三元桥", "Sanyuanqiao", 39.9547, 116.4498, listOf("Line10", "AirportExpress"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "ATM", "Coffee Shop"), listOf("300", "401", "847"), listOf("Phoenix Town Mall", "Third Embassy Area")),
        "shuangjing" to Station("shuangjing", "双井", "Shuangjing", 39.8906, 116.4532, listOf("Line10"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom", "Bakery"), listOf("23", "300", "57"), listOf("Viva Plaza", "Fuli Town")),
        "mudanyuan" to Station("mudanyuan", "牡丹园", "Mudanyuan", 39.9721, 116.3638, listOf("Line10"), listOf("A", "B", "C", "D"), true, true, listOf("Restroom"), listOf("16", "47", "331"), listOf("Mudanyuan Park", "Cuiwei Department Store")),
        "bagou" to Station("bagou", "巴沟", "Bagou", 39.9669, 116.2905, listOf("Line10"), listOf("A", "B", "C"), true, true, listOf("Restroom", "ATM"), listOf("528", "539"), listOf("Zhongguancun Shopping Center", "Yiheyuan (Summer Palace)")),

        // Airport & Daxing Lines
        "airport_t3" to Station("airport_t3", "3号航站楼", "Terminal 3", 40.0526, 116.6025, listOf("AirportExpress"), listOf("Gate 3"), true, true, listOf("Restroom", "Family Room", "ATM", "Coffee Shop", "Baggage Storage"), listOf("Airport Shuttle"), listOf("Beijing Capital International Airport Terminal 3")),
        "airport_t2" to Station("airport_t2", "2号航站楼", "Terminal 2", 40.0651, 116.5891, listOf("AirportExpress"), listOf("Gate 2"), true, true, listOf("Restroom", "ATM", "Coffee Shop"), listOf("Airport Shuttle"), listOf("Beijing Capital International Airport Terminal 2")),
        "caoqiao" to Station("caoqiao", "草桥", "Caoqiao", 39.8428, 116.3429, listOf("Line10", "DaxingExpress"), listOf("A", "B", "E"), true, true, listOf("Restroom", "In-town Check-in", "ATM"), listOf("483", "646"), listOf("Caoqiao Flowers Market")),
        "daxingyinghai" to Station("daxingyinghai", "大兴新城", "Daxing New Town", 39.7348, 116.3312, listOf("DaxingExpress"), listOf("A", "B"), true, true, listOf("Restroom"), listOf("兴59", "兴36"), listOf("Daxing Central Park")),
        "daxing_airport" to Station("daxing_airport", "大兴机场", "Daxing Airport", 39.5015, 116.4116, listOf("DaxingExpress"), listOf("Domestic Arrivals", "International Arrivals"), true, true, listOf("Restroom", "Family Room", "ATM", "Daxing Airport CIP Lounge"), listOf("Airport Bus"), listOf("Beijing Daxing International Airport"))
    )

    val lines = listOf(
        Line("Line1", "1号线/八通线", "Line 1 / Batong Line", MetroColors.Line1, "#A42422", listOf("pingguoyuan", "gongzhufen", "fuxingmen", "xidan", "tiananmen_west", "tiananmen_east", "dongdan", "guomao", "sihui", "universal_resort")),
        Line("Line2", "2号线 (环线)", "Line 2 (Loop)", MetroColors.Line2, "#005691", listOf("xizhimen", "guloudajie", "yonghegong", "dongzhimen", "chaoyangmen", "jianguomen", "beijing_railway", "qianmen", "xuanwumen", "xizhimen")), // complete the loop representation
        Line("Line4", "4号线/大兴线", "Line 4 / Daxing Line", MetroColors.Line4, "#007E7A", listOf("anheqiao_north", "zhongguancun", "haidianhuangzhuang", "national_library", "xizhimen", "pinganli", "xidan", "xuanwumen", "beijing_south", "jiaomen_west", "tiangongyuan")),
        Line("Line5", "5号线", "Line 5", MetroColors.Line5, "#7F1070", listOf("tiantongyuan_north", "huixinxijie_nankou", "yonghegong", "dongdan", "chongwenmen", "tiantandongmen", "songjiazhuang")),
        Line("Line10", "10号线 (环线)", "Line 10 (Loop)", MetroColors.Line10, "#0096D1", listOf("bagou", "haidianhuangzhuang", "gongzhufen", "caoqiao", "jiaomen_west", "songjiazhuang", "shuangjing", "guomao", "sanyuanqiao", "huixinxijie_nankou", "beitucheng", "mudanyuan", "bagou")),
        Line("AirportExpress", "首都机场线", "Capital Airport Express", MetroColors.AirportExpress, "#904F95", listOf("dongzhimen", "sanyuanqiao", "airport_t3", "airport_t2", "dongzhimen")),
        Line("DaxingExpress", "大兴机场线", "Daxing Airport Express", MetroColors.DaxingExpress, "#1E3A8A", listOf("caoqiao", "daxingyinghai", "daxing_airport"))
    )

    // A comprehensive travel advisory announcements for "今日运营公告"
    val announcements = listOf(
        Advisory("1", "北京地铁全线运营正常。今日大兴机场线、首都机场线运行平稳，发车间隔正常。", "All Beijing Metro lines are operating normally today. Capital Airport Express and Daxing Airport Express are running smoothly with standard headways.", "2026-07-08 06:00", false),
        Advisory("2", "【重要通知】配合环球度假区暑期客流高峰，1号线八通线将加开临客，缩短发车时间，请听从站内人员指挥。", "[Important] In coordination with the summer tourist peak at Universal Resort, Line 1/Batong Line will run extra trains to reduce waiting time. Please follow staff guidance.", "2026-07-07 18:20", false),
        Advisory("3", "【换乘提示】国贸站换乘10号线高峰期客流较多，建议在双井站或建国门站避峰换乘。", "[Transfer Notice] Guomao Station (transfer Line 1/10) expects heavy crowds during peak hours. Off-peak transfers at Shuangjing or Jianguomen are recommended.", "2026-07-08 07:45", true),
        Advisory("4", "【无障碍设备维护】西单站1号线通往4号线的无障碍直梯将于今日10:00-12:00进行常规保养维护，请有需要的乘客联系站内服务热线。", "[Elevator Maintenance] The accessible elevator connecting Line 1 and Line 4 at Xidan Station will undergo routine maintenance today from 10:00-12:00.", "2026-07-08 08:00", false)
    )

    data class Advisory(
        val id: String,
        val contentZh: String,
        val contentEn: String,
        val time: String,
        val isUrgent: Boolean
    )

    // Tourist recommended routes (游客模式)
    val touristRoutes = listOf(
        TouristRoute(
            "1",
            "【经典地标】北京中轴线一日游",
            "[Classic Landmarks] Beijing Central Axis One-Day Tour",
            "tiananmen_east",
            "qianmen",
            "天安门东 (1号线) -> 故宫 -> 景山公园 -> 步行至前门 (2号线) 品尝地道美食",
            "Tian'anmen East (Line 1) -> Palace Museum -> Jingshan Park -> Walk to Qianmen (Line 2) for authentic snacks",
            "Line1",
            listOf("tiananmen_east", "tiananmen_west", "qianmen")
        ),
        TouristRoute(
            "2",
            "【皇家园林与学术】颐和园与名校行",
            "[Royal Garden & Campus] Summer Palace & Top Universities",
            "bagou",
            "zhongguancun",
            "巴沟站 (10号线) -> 换乘西郊线至颐和园 -> 乘4号线至中关村 (北京大学、清华大学)",
            "Bagou (Line 10) -> West Suburb Line to Summer Palace -> Line 4 to Zhongguancun (Peking & Tsinghua University Campus)",
            "Line4",
            listOf("bagou", "zhongguancun", "anheqiao_north")
        ),
        TouristRoute(
            "3",
            "【合家欢度假】北京环球度假区奇妙之旅",
            "[Family Vacation] Universal Studios Magical Journey",
            "guomao",
            "universal_resort",
            "国贸站 (1号线/10号线) -> 乘坐1号线八通线直达 环球度假区站",
            "Guomao (Line 1 / 10) -> Take Line 1/Batong directly to Universal Resort Station",
            "Line1",
            listOf("guomao", "sihui", "universal_resort")
        ),
        TouristRoute(
            "4",
            "【历史文化】雍和宫与老北京胡同",
            "[History & Culture] Yonghe Lama Temple & Hutong Alleyways",
            "yonghegong",
            "pinganli",
            "雍和宫站 (2/5号线) -> 参观雍和宫、国子监 -> 乘5号线至东单转1号线到西单转4号线到平安里，逛什刹海胡同",
            "Yonghegong (Line 2/5) -> Visit Lama Temple & Guozijian -> Line 5 to Dongdan, transfer to Line 1 to Xidan, transfer to Line 4 to Pinganli for Houhai Hutongs",
            "Line5",
            listOf("yonghegong", "pinganli", "guloudajie")
        )
    )

    data class TouristRoute(
        val id: String,
        val titleZh: String,
        val titleEn: String,
        val startStationId: String,
        val endStationId: String,
        val guideZh: String,
        val guideEn: String,
        val primaryLineId: String,
        val featuredStations: List<String>
    )

    // FAQs for English and Chinese Help
    val faqs = listOf(
        FaqItem(
            "1",
            "北京地铁单程票如何购买？",
            "How do I buy a single journey ticket for the Beijing Metro?",
            "可在车站自动售票机(TVM)使用现金、微信、支付宝购买，或在手机App内开通电子乘车码直接刷码乘车。",
            "You can buy tickets with Cash, WeChat Pay, or Alipay at Ticket Vending Machines (TVMs) inside any station, or simply use the Electronic Ride QR Code inside this App to scan and ride."
        ),
        FaqItem(
            "2",
            "如何换乘机场快线？",
            "How do I transfer to Airport Express Lines?",
            "首都机场线可在2号线/13号线东直门站，或10号线三元桥站进行换乘；大兴机场线可在10号线/19号线草桥站进行换乘，大兴机场线支持在草桥站进行城市航站楼提前值机和行李托运。",
            "Capital Airport Express transfers from Line 2/13 at Dongzhimen, or Line 10 at Sanyuanqiao. Daxing Airport Express transfers from Line 10/19 at Caoqiao Station, where you can check-in baggage early in the Town Check-in Hall."
        ),
        FaqItem(
            "3",
            "北京地铁票价是如何计算的？",
            "How is the Beijing Metro fare calculated?",
            "采用分段计价制：起步6公里内3元；6至12公里4元；12至22公里5元；22至32公里6元；32公里以上每增加20公里增加1元。机场快线为单一票价（首都机场线25元，大兴机场线普通车单程35元）。",
            "Distance-based pricing: 0-6 km is ¥3; 6-12 km is ¥4; 12-22 km is ¥5; 22-32 km is ¥6; over 32 km is ¥1 for every extra 20 km. Airport express services have flat fares (Capital Express is ¥25, Daxing Express ordinary one-way is ¥35)."
        ),
        FaqItem(
            "4",
            "北京地铁首末班车时间一般是什么时候？",
            "What are the general operating hours of the Beijing Metro?",
            "大部分线路首班车在早晨 05:00 - 05:30 之间，末班车在晚上 22:30 - 23:30 之间。节假日或特殊客流日，部分线路（如1号线、4号线等）会延长运营时间，请关注App实时首末班车时间板块。",
            "Most lines start operations between 05:00 and 05:30, and end between 22:30 and 23:30. During holidays or special events, key lines (like Line 1 or 4) may extend operations. Please check the real-time scheduled hours tab."
        )
    )

    data class FaqItem(
        val id: String,
        val questionZh: String,
        val questionEn: String,
        val answerZh: String,
        val answerEn: String
    )

    /**
     * Finds the optimal path between startStationId and endStationId using BFS (Breadth-First Search).
     * Calculates ticket price based on geographical distance proxy or standard station counts, transfer lines, leg details, and estimated durations.
     */
    fun planRoute(startId: String, endId: String): PlanResult? {
        val startStation = stations[startId] ?: return null
        val endStation = stations[endId] ?: return null

        if (startId == endId) {
            return PlanResult(
                startStation, endStation, listOf(startStation), emptyList(), 0, 3, 0,
                "出发站与到达站相同", "Start and destination stations are the same", emptyList()
            )
        }

        // We build an adjacency list based on our lines connections
        val adj = mutableMapOf<String, MutableSet<String>>()
        stations.keys.forEach { adj[it] = mutableSetOf() }

        for (line in lines) {
            for (i in 0 until line.stations.size - 1) {
                val s1 = line.stations[i]
                val s2 = line.stations[i + 1]
                if (adj.containsKey(s1) && adj.containsKey(s2)) {
                    adj[s1]!!.add(s2)
                    adj[s2]!!.add(s1)
                }
            }
        }

        // BFS to find the shortest path in terms of station stops
        val queue = ArrayDeque<List<String>>()
        queue.add(listOf(startId))
        val visited = mutableSetOf(startId)
        var shortestPath: List<String>? = null

        while (queue.isNotEmpty()) {
            val currPath = queue.removeFirst()
            val lastNode = currPath.last()

            if (lastNode == endId) {
                shortestPath = currPath
                break
            }

            val neighbors = adj[lastNode] ?: emptySet()
            for (neighbor in neighbors) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    val newPath = currPath.toMutableList()
                    newPath.add(neighbor)
                    queue.add(newPath)
                }
            }
        }

        if (shortestPath == null) return null

        // Convert path station IDs to Station objects
        val routeStations = shortestPath.map { stations[it]!! }

        // Determine lines and transfers
        val legs = mutableListOf<RouteLeg>()
        val lineChanges = mutableListOf<String>()
        var currentLineId = getCommonLine(routeStations[0], routeStations[1]) ?: "Line1"
        lineChanges.add(currentLineId)

        var legStart = routeStations[0]
        var legStationCount = 1

        for (i in 1 until routeStations.size) {
            val s1 = routeStations[i - 1]
            val s2 = routeStations[i]
            val lineConnecting = getCommonLine(s1, s2) ?: currentLineId

            if (lineConnecting != currentLineId) {
                // Transfer happened
                val duration = legStationCount * 3
                legs.add(RouteLeg(legStart, s1, currentLineId, legStationCount - 1, duration))
                lineChanges.add(lineConnecting)
                currentLineId = lineConnecting
                legStart = s1
                legStationCount = 1
            }
            legStationCount++
        }
        // Add final leg
        val finalDuration = (legStationCount - 1) * 3
        legs.add(RouteLeg(legStart, routeStations.last(), currentLineId, legStationCount - 1, finalDuration))

        val transfersCount = lineChanges.size - 1
        val numStationsTotal = routeStations.size - 1
        val totalTimeMin = numStationsTotal * 3 + transfersCount * 4 // 3 mins per station, 4 mins per transfer

        // Pricing logic: 0-6 km corresponds to standard pricing, we can model it by station counts:
        // 1-4 stations: 3RMB, 5-10 stations: 4RMB, 11-18 stations: 5RMB, 19+: 6RMB.
        // If AirportExpress is used, it adds 25RMB or DaxingExpress adds 35RMB.
        var basePrice = when {
            numStationsTotal <= 4 -> 3
            numStationsTotal <= 10 -> 4
            numStationsTotal <= 18 -> 5
            else -> 6
        }
        if (lineChanges.contains("AirportExpress")) {
            basePrice = 25
        } else if (lineChanges.contains("DaxingExpress")) {
            basePrice = 35
        }

        val pathDescZh = buildString {
            append("途径 ${numStationsTotal} 站，换乘 ${transfersCount} 次。")
            lineChanges.forEachIndexed { idx, lineId ->
                val lineName = lines.find { it.id == lineId }?.nameZh ?: ""
                if (idx == 0) append("搭乘$lineName")
                else append("，并在【${legs[idx].fromStation.nameZh}】换乘$lineName")
            }
        }

        val pathDescEn = buildString {
            append("Via ${numStationsTotal} stops, ${transfersCount} transfers. ")
            lineChanges.forEachIndexed { idx, lineId ->
                val lineName = lines.find { it.id == lineId }?.nameEn ?: ""
                if (idx == 0) append("Take $lineName")
                else append(", transfer at [${legs[idx].fromStation.nameEn}] to $lineName")
            }
        }

        return PlanResult(
            startStation = startStation,
            endStation = endStation,
            routeList = routeStations,
            lineChanges = lineChanges,
            totalTimeMin = totalTimeMin,
            ticketPrice = basePrice,
            transfersCount = transfersCount,
            descriptionZh = pathDescZh,
            descriptionEn = pathDescEn,
            legs = legs
        )
    }

    private fun getCommonLine(s1: Station, s2: Station): String? {
        val common = s1.lines.intersect(s2.lines)
        return common.firstOrNull()
    }
}
