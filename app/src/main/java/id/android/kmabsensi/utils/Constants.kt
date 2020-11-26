package id.android.kmabsensi.utils

const val ROLE_ADMIN = "admin"
const val ROLE_MANAGEMEMENT = "management"
const val ROLE_SDM = "sdm"

const val OFFICE_KEY = "office"
const val COWORKING_KEY = "coworking"
const val USER_KEY = "user"
const val USER_ID_KEY = "userId"
const val IS_MANAGEMENT_KEY = "isManagement"
const val USER_MANAGEMENT_NAME_KEY = "username"
const val DATA_OFFICE_KEY = "date_office"
const val PRESENCE_ID_KEY = "presence_id"
const val IS_CHECKIN_KEY = "is_checkin"
const val DASHBOARD_DATA_KEY = "dashboard_data"

const val DATE_FILTER_KEY = "date_fitler"
const val END_DATE_FILTER_KEY = "end_date_fitler"
const val OFFICE_ID_FILTER = "office_id_filter"
const val OFFICE_NAME_FILTER = "office_name_filter"

const val CATEGORY_REPORT_KEY = "category_report"
const val PERMISSION_DATA_KEY = "permission"
const val IS_FROM_MANAJEMEN_IZI = "is_form_manajemen_izin"

const val IS_SORT_KEY = "is_sort"
const val PARTNER_DATA_KEY = "partner_data_key"
const val NO_PARTNER_KEY = "no_partner_key"
const val NAME_PARTNER_KEY = "name_partner_key"
const val SIMPLE_PARTNER_DATA_KEY = "simple_partner_data_key"

const val INVOICE_ID_KEY = "invoice_id"
const val INVOICE_DATA_KEY = "invoice_data"
const val INVOICE_TYPE_KEY = "invoice_type"
const val IS_INVOICE_ADMIN_KEY = "is_invoice_admin"
const val EVALUATION_KEY = "evaluation"

const val SDM_KEY = "sdm_key"

const val PARTNER_RESPONSE_KEY = "partner_response"

const val DEVICE_DATA = "device_data"
const val ADMINISTRATION_DATA = "administration_data"
const val PRODUCT_KNOWLEDGE_KEY = "product_knowledge"

const val MESSAGE_CRUD = "message_crud"

const val START_PERIOD = "start_period"
const val END_PERIOD = "end_period"
const val INVOICE_TYPE = "invoice_type"
const val INVOICE_STATUS = "invoice_status"
const val LEADER_ID = "leader_id"

enum class SORT_TYPE {
    LEADS,  TRANSACTION, ORDER, RATE_CONVERSION, RATE_ORDER
}


// tanggal format = yyyy-MM-dd
const val JADWAL_SHOLAT_URL = "https://api.banghasan.com/sholat/format/json/jadwal/kota/725/tanggal/"