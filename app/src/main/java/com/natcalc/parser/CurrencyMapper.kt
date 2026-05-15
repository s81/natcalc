package com.natcalc.parser

object CurrencyMapper {

    val aliases: Map<String, String> = mapOf(
        // English
        "dollar" to "USD", "dollars" to "USD", "usd" to "USD", "us dollar" to "USD",
        "euro" to "EUR", "euros" to "EUR", "eur" to "EUR",
        "pound" to "GBP", "pounds" to "GBP", "gbp" to "GBP", "sterling" to "GBP",
        "yen" to "JPY", "jpy" to "JPY",
        "yuan" to "CNY", "renminbi" to "CNY", "cny" to "CNY",
        "franc" to "CHF", "francs" to "CHF", "chf" to "CHF",
        "dinar" to "JOD", "dinars" to "JOD", "jod" to "JOD",
        "kuwaiti dinar" to "KWD", "kwd" to "KWD",
        "bahraini dinar" to "BHD", "bhd" to "BHD",
        "dirham" to "AED", "dirhams" to "AED", "aed" to "AED",
        "riyal" to "SAR", "riyals" to "SAR", "sar" to "SAR",
        "qatari riyal" to "QAR", "qar" to "QAR",
        "omani riyal" to "OMR", "omr" to "OMR",
        "rupee" to "INR", "rupees" to "INR", "inr" to "INR",
        "lira" to "TRY", "liras" to "TRY", "try" to "TRY",
        "egyptian pound" to "EGP", "egp" to "EGP",
        "canadian dollar" to "CAD", "cad" to "CAD",
        "australian dollar" to "AUD", "aud" to "AUD",
        // Arabic
        "دولار" to "USD", "دولارات" to "USD",
        "يورو" to "EUR",
        "جنيه إسترليني" to "GBP",
        "جنيه" to "EGP",
        "ين" to "JPY",
        "يوان" to "CNY",
        "فرنك" to "CHF",
        "دينار أردني" to "JOD",
        "دينار كويتي" to "KWD",
        "دينار بحريني" to "BHD",
        "دينار عراقي" to "IQD",
        "دينار" to "JOD",
        "درهم إماراتي" to "AED",
        "درهم مغربي" to "MAD",
        "درهم" to "AED",
        "ريال سعودي" to "SAR",
        "ريال قطري" to "QAR",
        "ريال عماني" to "OMR",
        "ريال" to "SAR",
        "روبية" to "INR",
        "ليرة تركية" to "TRY",
        "ليرة لبنانية" to "LBP",
        "ليرة سورية" to "SYP",
        "ليرة" to "TRY",
        "دولار كندي" to "CAD",
        "دولار أسترالي" to "AUD"
    )

    // Rates relative to USD (1 USD = X of this currency)
    val mockRatesFromUsd: Map<String, Double> = mapOf(
        "USD" to 1.0, "EUR" to 0.92, "GBP" to 0.79, "JPY" to 149.5,
        "CNY" to 7.24, "CHF" to 0.89, "JOD" to 0.71, "KWD" to 0.307,
        "BHD" to 0.377, "AED" to 3.67, "SAR" to 3.75, "QAR" to 3.64,
        "OMR" to 0.385, "INR" to 83.1, "TRY" to 30.5, "EGP" to 30.9,
        "IQD" to 1310.0, "LBP" to 15000.0, "SYP" to 13000.0,
        "CAD" to 1.36, "AUD" to 1.53, "NZD" to 1.63, "SGD" to 1.34,
        "HKD" to 7.82, "MAD" to 10.05, "TND" to 3.12, "DZD" to 134.5,
        "LYD" to 4.83, "PKR" to 279.5, "BDT" to 110.0
    )

    val arabicNames: Map<String, String> = mapOf(
        "USD" to "دولار", "EUR" to "يورو", "GBP" to "جنيه إسترليني",
        "JPY" to "ين", "CNY" to "يوان", "CHF" to "فرنك",
        "JOD" to "دينار أردني", "KWD" to "دينار كويتي", "BHD" to "دينار بحريني",
        "AED" to "درهم إماراتي", "SAR" to "ريال سعودي", "QAR" to "ريال قطري",
        "OMR" to "ريال عماني", "INR" to "روبية", "TRY" to "ليرة تركية",
        "EGP" to "جنيه مصري", "IQD" to "دينار عراقي", "LBP" to "ليرة لبنانية",
        "CAD" to "دولار كندي", "AUD" to "دولار أسترالي", "MAD" to "درهم مغربي"
    )

    fun resolve(name: String): String? = aliases[name.lowercase().trim()]

    fun getDisplayName(code: String, isArabic: Boolean): String =
        if (isArabic) arabicNames[code] ?: code else code

    fun computeMockRate(from: String, to: String): Double? {
        val fromRate = mockRatesFromUsd[from] ?: return null
        val toRate = mockRatesFromUsd[to] ?: return null
        return toRate / fromRate
    }
}
