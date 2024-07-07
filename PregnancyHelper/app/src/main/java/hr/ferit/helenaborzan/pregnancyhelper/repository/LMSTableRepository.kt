package hr.ferit.helenaborzan.pregnancyhelper.repository

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

class LMSTableRepository (private val context : Context){
    private val calculationTable = mapOf(
        Pair("length_age", "male") to "LMS_boys_length_for_age.csv",
        Pair("length_age", "female") to "LMS_girls_length_for_age.csv",
        Pair("weight_age", "male") to "LMS_boys_weight_for_age.csv",
        Pair("weight_age", "female") to "LMS_girls_weight_for_age.csv",
        Pair("weight_length", "male") to "LMS_boys_weight_for_length.csv",
        Pair("weight_length", "female") to "LMS_girls_weight_for_length.csv",
        Pair("head_circumference_for_age", "male") to "LMS_boys_head_circumference_for_age.csv",
        Pair("head_circumference_for_age", "female") to "LMS_girls_head_circumference_for_age.csv"
    )

    private fun getTableData(type : String, sex : String) : List<List<String>>?{
        val tableFileName = calculationTable[Pair(type, sex)] ?: return null
        val inputStream = context.assets.open(tableFileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val tableData = mutableListOf<List<String>>()

        reader.use {
            it.forEachLine { line ->
                val row = line.split(";")
                tableData.add(row)
            }
        }
        return tableData
    }

    fun <T>getLMS(type : String, sex : String, searchCriteria : T) : List<Double>?{
       val tableData = getTableData(type, sex) ?: null
        if (tableData != null) {
            for (row in tableData) {
                if (row.isNotEmpty() && row[0] == searchCriteria.toString()) {
                    return row.drop(1).mapNotNull { it.replace(",", ".").toDoubleOrNull() }
                }
            }
        }
        println("No matching LMS values found for type: $type, sex: $sex, criteria: $searchCriteria")
        return null
    }
}