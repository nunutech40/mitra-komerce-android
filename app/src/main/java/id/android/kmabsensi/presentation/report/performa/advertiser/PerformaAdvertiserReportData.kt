package id.android.kmabsensi.presentation.report.performa.advertiser

import id.android.kmabsensi.data.remote.response.AdvertiserReport
import id.android.kmabsensi.utils.convertRpWithoutSpace

data class PerformaAdvertiserReport(
    val indikator: String = "",
    var today: String = "",
    var yesterday: String = "",
    var last7days: String = "",
    var thisMonth: String = "",
    var lastMonth: String = ""
)

object PerformaAdvertiserReportData {

    private val reports = mutableListOf<PerformaAdvertiserReport>()

    fun generateData() {
        reports.clear()
        reports.add(PerformaAdvertiserReport(indikator = "Tayangan"))
        reports.add(PerformaAdvertiserReport(indikator = "Klik Iklan"))
        reports.add(PerformaAdvertiserReport(indikator = "Visitor"))
        reports.add(PerformaAdvertiserReport(indikator = "Klik Kontak"))
        reports.add(PerformaAdvertiserReport(indikator = "Leads"))
        reports.add(PerformaAdvertiserReport(indikator = "Biaya Iklan"))
        reports.add(PerformaAdvertiserReport(indikator = "CTR Link"))
        reports.add(PerformaAdvertiserReport(indikator = "Rasio LP"))
        reports.add(PerformaAdvertiserReport(indikator = "CPR"))
    }

    fun setToday(report: AdvertiserReport){
        reports.find { it.indikator.toLowerCase() == "tayangan" }?.today = report.totalView.toString()
        reports.find { it.indikator.toLowerCase() == "klik iklan" }?.today = report.totalAdClick.toString()
        reports.find { it.indikator.toLowerCase() == "visitor" }?.today = report.totalVisitor.toString()
        reports.find { it.indikator.toLowerCase() == "klik kontak" }?.today = report.totalContactClick.toString()
        reports.find { it.indikator.toLowerCase() == "leads" }?.today = report.totalLeadsCs.toString()
        reports.find { it.indikator.toLowerCase() == "biaya iklan" }?.today = convertRpWithoutSpace(report.adCost.toDouble())
        reports.find { it.indikator.toLowerCase() == "ctr link" }?.today = "${report.ctrLink}%"
        reports.find { it.indikator.toLowerCase() == "rasio lp" }?.today = "${report.ratioLp}%"
        reports.find { it.indikator.toLowerCase() == "cpr" }?.today = convertRpWithoutSpace(report.cpr.toDouble())
    }

    fun setYesterday(report: AdvertiserReport){
        reports.find { it.indikator.toLowerCase() == "tayangan" }?.yesterday = report.totalView.toString()
        reports.find { it.indikator.toLowerCase() == "klik iklan" }?.yesterday = report.totalAdClick.toString()
        reports.find { it.indikator.toLowerCase() == "visitor" }?.yesterday = report.totalVisitor.toString()
        reports.find { it.indikator.toLowerCase() == "klik kontak" }?.yesterday = report.totalContactClick.toString()
        reports.find { it.indikator.toLowerCase() == "leads" }?.yesterday = report.totalLeadsCs.toString()
        reports.find { it.indikator.toLowerCase() == "biaya iklan" }?.yesterday = convertRpWithoutSpace(report.adCost.toDouble())
        reports.find { it.indikator.toLowerCase() == "ctr link" }?.yesterday = "${report.ctrLink}%"
        reports.find { it.indikator.toLowerCase() == "rasio lp" }?.yesterday = "${report.ratioLp}%"
        reports.find { it.indikator.toLowerCase() == "cpr" }?.yesterday = convertRpWithoutSpace(report.cpr.toDouble())
    }

    fun setLast7days(report: AdvertiserReport){
        reports.find { it.indikator.toLowerCase() == "tayangan" }?.last7days = report.totalView.toString()
        reports.find { it.indikator.toLowerCase() == "klik iklan" }?.last7days = report.totalAdClick.toString()
        reports.find { it.indikator.toLowerCase() == "visitor" }?.last7days = report.totalVisitor.toString()
        reports.find { it.indikator.toLowerCase() == "klik kontak" }?.last7days = report.totalContactClick.toString()
        reports.find { it.indikator.toLowerCase() == "leads" }?.last7days = report.totalLeadsCs.toString()
        reports.find { it.indikator.toLowerCase() == "biaya iklan" }?.last7days = convertRpWithoutSpace(report.adCost.toDouble())
        reports.find { it.indikator.toLowerCase() == "ctr link" }?.last7days = "${report.ctrLink}%"
        reports.find { it.indikator.toLowerCase() == "rasio lp" }?.last7days = "${report.ratioLp}%"
        reports.find { it.indikator.toLowerCase() == "cpr" }?.last7days = convertRpWithoutSpace(report.cpr.toDouble())
    }

    fun setThisMonth(report: AdvertiserReport){
        reports.find { it.indikator.toLowerCase() == "tayangan" }?.thisMonth = report.totalView.toString()
        reports.find { it.indikator.toLowerCase() == "klik iklan" }?.thisMonth = report.totalAdClick.toString()
        reports.find { it.indikator.toLowerCase() == "visitor" }?.thisMonth = report.totalVisitor.toString()
        reports.find { it.indikator.toLowerCase() == "klik kontak" }?.thisMonth = report.totalContactClick.toString()
        reports.find { it.indikator.toLowerCase() == "leads" }?.thisMonth = report.totalLeadsCs.toString()
        reports.find { it.indikator.toLowerCase() == "biaya iklan" }?.thisMonth = convertRpWithoutSpace(report.adCost.toDouble())
        reports.find { it.indikator.toLowerCase() == "ctr link" }?.thisMonth = "${report.ctrLink}%"
        reports.find { it.indikator.toLowerCase() == "rasio lp" }?.thisMonth = "${report.ratioLp}%"
        reports.find { it.indikator.toLowerCase() == "cpr" }?.thisMonth = convertRpWithoutSpace(report.cpr.toDouble())
    }

    fun setLastMonth(report: AdvertiserReport){
        reports.find { it.indikator.toLowerCase() == "tayangan" }?.lastMonth = report.totalView.toString()
        reports.find { it.indikator.toLowerCase() == "klik iklan" }?.lastMonth = report.totalAdClick.toString()
        reports.find { it.indikator.toLowerCase() == "visitor" }?.lastMonth = report.totalVisitor.toString()
        reports.find { it.indikator.toLowerCase() == "klik kontak" }?.lastMonth = report.totalContactClick.toString()
        reports.find { it.indikator.toLowerCase() == "leads" }?.lastMonth = report.totalLeadsCs.toString()
        reports.find { it.indikator.toLowerCase() == "biaya iklan" }?.lastMonth = convertRpWithoutSpace(report.adCost.toDouble())
        reports.find { it.indikator.toLowerCase() == "ctr link" }?.lastMonth = "${report.ctrLink}%"
        reports.find { it.indikator.toLowerCase() == "rasio lp" }?.lastMonth = "${report.ratioLp}%"
        reports.find { it.indikator.toLowerCase() == "cpr" }?.lastMonth = convertRpWithoutSpace(report.cpr.toDouble())
    }

    fun getData(): List<PerformaAdvertiserReport>{
        return reports
    }

}