package net.catenax.core.managedidentitywallets.plugins

import io.ktor.http.*

// for 2.0.0-beta
// import io.ktor.server.routing.*
// import io.ktor.server.application.*
// import io.ktor.server.response.*
// import io.ktor.server.request.*

// for 1.6.7
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.http.content.*
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.sessions.*

import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.features.*
import io.ktor.client.*

import java.io.File
import java.io.IOException

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

import io.bkbn.kompendium.core.routes.redoc

import net.catenax.core.managedidentitywallets.models.*
import net.catenax.core.managedidentitywallets.services.WalletService

suspend fun retrieveBusinessPartnerInfo(datapoolUrl: String, bpn: String, token: String): String {

    var stringBody: String = ""
    HttpClient(Apache).use { client ->
        val httpResponse: HttpResponse = client.get("$datapoolUrl/$bpn") {
            headers {
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        stringBody = httpResponse.readText()
    }

    return stringBody
}

@Serializable
data class BusinessPartnerInfo(val bpn: String)

fun Application.configureRouting(walletService: WalletService) {

    val datapoolUrl = environment.config.property("datapool.url").getString()

    routing {

        redoc(pageTitle = "Managed Identity Wallets API Docs")

        get("/") {
            call.respondText(
"""
<html>
  <head>
    <title>Catena-X Core // Managed Identity Wallets</title>
    <style>
      body {
        font-family:sans-serif;
        padding: 10px;
        max-width: 600px;
        line-height: 150%;
      }
      a {
        color: #000;
        text-decoration: none;
      }
      a:hover {
        text-decoration: underline;
      }
      icon {
        font-family: icomoon;
        text-align: center;
        font-style: normal;
      }
      .d-block {
        display: block!important;
      }
      svg.d-xl-none {
        width: 205px;
        height: 48px;
        margin-bottom: 16px;
      }
    </style>
  </head>
  <body>
    <svg width="0" height="0" class="hidden">
        <symbol fill="none" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 239 56" id="logo-head-xxl">
        <g clip-path="url(#clip0_1185_4388)">
            <path d="M40.052 28.8289V22.7998C40.0493 22.6197 39.9984 22.4436 39.9046 22.29C39.8109 22.1365 39.6777 22.011 39.519 21.9268C39.3604 21.8425 39.1821 21.8026 39.0028 21.8113C38.8235 21.8199 38.6499 21.8767 38.5 21.9758L14.7091 38.4955C14.0916 38.9247 13.3688 39.176 12.619 39.2224C11.8693 39.2687 11.1212 39.1082 10.4559 38.7583C9.79055 38.4085 9.23333 37.8825 8.84461 37.2374C8.4559 36.5924 8.25052 35.8529 8.25073 35.0991V27.1307C8.25232 25.7945 8.57652 24.4786 9.19557 23.2956C9.81462 22.1126 10.7101 21.0978 11.8053 20.3379L18.8945 15.4242C19.438 15.0483 19.8823 14.5455 20.1892 13.9592C20.4962 13.3728 20.6567 12.7204 20.6568 12.058V6.41078V0.994664C20.6502 0.818549 20.5976 0.647272 20.5042 0.49802C20.4108 0.348769 20.2799 0.226793 20.1247 0.144333C19.9695 0.0618722 19.7954 0.021828 19.6199 0.0282182C19.4444 0.0346085 19.2737 0.0872084 19.1248 0.18074L14.1183 3.61731C13.5759 4.00315 13.1355 4.51602 12.8354 5.11139C12.5353 5.70677 12.3845 6.36666 12.3961 7.03378V9.88754L6.30821 14.0677C4.3619 15.4143 2.77083 17.2153 1.67195 19.3159C0.573069 21.4164 -0.000700537 23.7534 2.96616e-05 26.1258V35.0589C-0.00491576 37.3261 0.608632 39.5514 1.77416 41.4936C2.9397 43.4358 4.61275 45.0207 6.61195 46.0767C8.61115 47.1326 10.8602 47.6192 13.1154 47.4838C15.3706 47.3483 17.5458 46.596 19.4052 45.3083L38.3297 32.2453C38.8715 31.8589 39.3115 31.3459 39.6115 30.7507C39.9116 30.1554 40.0627 29.4958 40.052 28.8289V28.8289Z" fill="#FFA600"></path>
            <path d="M33.0428 10.6815L14.1183 23.7445C13.5751 24.1247 13.132 24.6314 12.8268 25.2212C12.5216 25.811 12.3635 26.4662 12.366 27.1308V33.1599C12.3705 33.3391 12.4226 33.5139 12.5171 33.6661C12.6116 33.8183 12.7449 33.9423 12.9032 34.0254C13.0616 34.1084 13.2392 34.1474 13.4176 34.1384C13.5961 34.1293 13.7688 34.0725 13.918 33.9738L37.7589 17.4944C38.3764 17.0679 39.0982 16.8188 39.8464 16.7739C40.5946 16.729 41.3408 16.8901 42.0045 17.2396C42.6681 17.5892 43.2241 18.114 43.6123 18.7575C44.0005 19.4009 44.2063 20.1385 44.2073 20.8907V28.829C44.2056 30.1666 43.8815 31.4839 43.2625 32.6685C42.6435 33.8531 41.7481 34.8698 40.6527 35.6318L34.3145 40.0129L33.5935 40.5153C33.0527 40.8908 32.6108 41.3924 32.3056 41.9769C32.0004 42.5615 31.8411 43.2116 31.8413 43.8715V45.419V54.9449C31.8457 55.1242 31.8979 55.2989 31.9924 55.4511C32.0868 55.6033 32.2201 55.7274 32.3785 55.8104C32.5368 55.8935 32.7144 55.9325 32.8929 55.9234C33.0713 55.9143 33.2441 55.8575 33.3933 55.7588L38.3998 52.3122C38.9227 51.9263 39.3457 51.4201 39.6332 50.8361C39.9207 50.2521 40.0643 49.6073 40.0519 48.956V46.0922L46.1198 41.892C48.0636 40.5437 49.6523 38.742 50.7493 36.6417C51.8463 34.5414 52.4189 32.2052 52.4179 29.8338V20.9008C52.4171 18.6396 51.8007 16.4216 50.6352 14.4864C49.4697 12.5512 47.7996 10.9724 45.8051 9.92039C43.8106 8.8684 41.5675 8.38328 39.3183 8.51742C37.0691 8.65156 34.8991 9.39986 33.0428 10.6815V10.6815Z" fill="#B3CB2D"></path>
            <path d="M76.2389 54.4626H72.9046L72.3539 55.8694H71.2324L73.9359 49.0465H75.2076L77.9111 55.8694H76.7896L76.2389 54.4626ZM73.235 53.5482H75.9786L74.5667 49.9408L73.235 53.5482Z" fill="black"></path>
            <path d="M82.417 55.1961C82.1949 55.4428 81.9249 55.6413 81.6235 55.7795C81.3222 55.9178 80.996 55.9928 80.6647 56C80.4521 56.0284 80.2357 56.0071 80.0326 55.9377C79.8295 55.8683 79.6451 55.7527 79.4939 55.5999C79.3426 55.4472 79.2286 55.2614 79.1608 55.0572C79.0929 54.8529 79.0731 54.6356 79.1027 54.4224V50.9356H80.0239V54.1109C80.0239 54.9349 80.4244 55.176 81.0252 55.176C81.2908 55.1697 81.5516 55.1042 81.7889 54.9843C82.0261 54.8644 82.2338 54.693 82.397 54.4827V50.9356H83.3182V55.8694H82.397L82.417 55.1961Z" fill="black"></path>
            <path d="M85.481 54.7841V51.7696H84.6699V50.9657H85.481V49.579H86.4022V50.9356H87.4035V51.7395H86.4022V54.5731C86.4022 54.9248 86.5624 55.176 86.8828 55.176C87.0679 55.1793 87.2472 55.1109 87.3834 54.9851L87.6137 55.6784C87.4776 55.7979 87.3186 55.8881 87.1463 55.9434C86.9741 55.9987 86.7924 56.018 86.6124 56C86.4542 56.0103 86.2956 55.9853 86.1482 55.9267C86.0007 55.868 85.8681 55.7773 85.7598 55.6609C85.6516 55.5446 85.5704 55.4056 85.5222 55.254C85.474 55.1024 85.4599 54.9419 85.481 54.7841V54.7841Z" fill="black"></path>
            <path d="M88.6249 53.3975C88.6445 52.9074 88.8072 52.4339 89.0929 52.0358C89.3785 51.6378 89.7744 51.3328 90.2313 51.1587C90.6883 50.9847 91.1861 50.9492 91.663 51.0568C92.1399 51.1644 92.5747 51.4103 92.9134 51.7639C93.2522 52.1175 93.4799 52.5632 93.5683 53.0456C93.6566 53.528 93.6017 54.0258 93.4104 54.4772C93.2191 54.9286 92.8999 55.3136 92.4923 55.5843C92.0847 55.855 91.6069 55.9996 91.1181 56C90.7805 56.0061 90.4452 55.9422 90.1333 55.8122C89.8214 55.6822 89.5396 55.489 89.3056 55.2447C89.0715 55.0004 88.8902 54.7102 88.7731 54.3924C88.656 54.0745 88.6055 53.7358 88.6249 53.3975V53.3975ZM92.6301 53.3975C92.5859 53.1041 92.4586 52.8296 92.2634 52.6068C92.0681 52.3839 91.8131 52.2221 91.5288 52.1406C91.2445 52.0592 90.9428 52.0614 90.6598 52.1472C90.3767 52.2329 90.1242 52.3985 89.9323 52.6243C89.7404 52.85 89.6172 53.1264 89.5774 53.4204C89.5376 53.7144 89.5829 54.0138 89.7079 54.2827C89.8329 54.5516 90.0322 54.7788 90.2823 54.9372C90.5323 55.0956 90.8224 55.1785 91.1181 55.176C91.3373 55.1678 91.5524 55.1141 91.75 55.0185C91.9476 54.9229 92.1233 54.7873 92.2662 54.6203C92.4091 54.4534 92.5161 54.2585 92.5804 54.0481C92.6448 53.8377 92.6651 53.6161 92.6401 53.3975H92.6301Z" fill="black"></path>
            <path d="M101.261 52.5735C101.261 52.0108 101.001 51.6289 100.4 51.6289C100.157 51.6443 99.9196 51.7157 99.7078 51.8377C99.496 51.9597 99.315 52.1289 99.1787 52.3323V55.8694H98.2575V52.5735C98.2575 52.0108 98.0172 51.6289 97.3964 51.6289C97.1541 51.6491 96.9194 51.7237 96.7097 51.8472C96.5 51.9707 96.3206 52.1399 96.1849 52.3424V55.8694H95.2737V50.9356H96.1849V51.6289C96.3753 51.3844 96.6162 51.1842 96.891 51.042C97.1658 50.8997 97.468 50.8189 97.7769 50.805C98.0726 50.7793 98.3681 50.8574 98.6128 51.026C98.8575 51.1946 99.0363 51.4432 99.1187 51.7294C99.2978 51.4492 99.5434 51.2181 99.8335 51.0567C100.124 50.8954 100.449 50.8089 100.781 50.805C101.682 50.805 102.173 51.3074 102.173 52.3022V55.8694H101.261V52.5735Z" fill="black"></path>
            <path d="M103.835 53.3975C103.854 52.9074 104.017 52.4339 104.303 52.0358C104.588 51.6378 104.984 51.3328 105.441 51.1587C105.898 50.9847 106.396 50.9492 106.873 51.0568C107.35 51.1644 107.784 51.4103 108.123 51.7639C108.462 52.1175 108.69 52.5632 108.778 53.0456C108.866 53.528 108.811 54.0258 108.62 54.4772C108.429 54.9286 108.11 55.3136 107.702 55.5843C107.294 55.855 106.817 55.9996 106.328 56C105.99 56.0061 105.655 55.9422 105.343 55.8122C105.031 55.6822 104.749 55.489 104.515 55.2447C104.281 55.0004 104.1 54.7102 103.983 54.3924C103.866 54.0745 103.815 53.7358 103.835 53.3975V53.3975ZM107.84 53.3975C107.796 53.1045 107.669 52.8304 107.474 52.6077C107.279 52.385 107.025 52.2231 106.741 52.1413C106.457 52.0595 106.156 52.0611 105.873 52.146C105.59 52.231 105.338 52.3956 105.145 52.6204C104.953 52.8452 104.829 53.1207 104.788 53.4141C104.747 53.7076 104.791 54.0066 104.914 54.2758C105.038 54.5449 105.236 54.7728 105.485 54.9324C105.733 55.092 106.023 55.1766 106.318 55.176C106.538 55.1691 106.754 55.1165 106.953 55.0215C107.152 54.9264 107.329 54.7911 107.473 54.6239C107.616 54.4568 107.724 54.2615 107.789 54.0504C107.854 53.8393 107.875 53.617 107.85 53.3975H107.84Z" fill="black"></path>
            <path d="M110.664 54.7841V51.7696H109.853V50.9657H110.664V49.579H111.585V50.9356H112.586V51.7395H111.585V54.5731C111.585 54.9248 111.745 55.176 112.065 55.176C112.25 55.1773 112.429 55.1092 112.566 54.9851L112.796 55.6784C112.66 55.7979 112.501 55.8881 112.329 55.9434C112.157 55.9987 111.975 56.018 111.795 56C111.637 56.0103 111.478 55.9853 111.331 55.9267C111.183 55.868 111.051 55.7773 110.942 55.6609C110.834 55.5446 110.753 55.4056 110.705 55.254C110.657 55.1024 110.643 54.9419 110.664 54.7841Z" fill="black"></path>
            <path d="M113.968 49.5188C113.97 49.3623 114.033 49.213 114.144 49.1024C114.254 48.9918 114.403 48.9285 114.559 48.9259C114.637 48.9245 114.714 48.9389 114.786 48.9682C114.859 48.9976 114.924 49.0412 114.979 49.0965C115.034 49.1518 115.078 49.2177 115.107 49.2903C115.136 49.3628 115.151 49.4405 115.149 49.5188C115.149 49.5961 115.134 49.6728 115.104 49.7441C115.074 49.8155 115.031 49.8802 114.976 49.9345C114.921 49.9887 114.855 50.0314 114.784 50.0601C114.712 50.0888 114.636 50.1029 114.559 50.1016C114.404 50.1016 114.255 50.0405 114.144 49.9315C114.034 49.8225 113.97 49.6742 113.968 49.5188V49.5188ZM114.108 50.9356H115.029V55.8694H114.148L114.108 50.9356Z" fill="black"></path>
            <path d="M116.271 50.9356H117.272L118.814 54.8143L120.356 50.9356H121.357L119.355 55.8694H118.353L116.271 50.9356Z" fill="black"></path>
            <path d="M124.732 50.805C125.07 50.8061 125.403 50.8781 125.712 51.0163C126.02 51.1546 126.297 51.356 126.523 51.6077C126.749 51.8594 126.92 52.1558 127.026 52.4778C127.131 52.7999 127.168 53.1405 127.135 53.4778V53.709H123.24C123.248 53.9215 123.297 54.1303 123.385 54.3237C123.474 54.517 123.599 54.6909 123.754 54.8356C123.91 54.9803 124.092 55.0928 124.29 55.1667C124.489 55.2406 124.7 55.2745 124.912 55.2665C125.181 55.272 125.448 55.2233 125.697 55.1231C125.946 55.023 126.173 54.8736 126.364 54.6837L126.795 55.2866C126.527 55.5376 126.211 55.7326 125.868 55.8603C125.524 55.9879 125.158 56.0457 124.792 56.0301C124.131 56.0021 123.507 55.7221 123.045 55.2472C122.583 54.7723 122.32 54.1385 122.308 53.4752C122.297 52.8119 122.538 52.1693 122.983 51.6786C123.428 51.1879 124.043 50.8862 124.702 50.8351L124.732 50.805ZM123.23 53.0357H126.234C126.235 52.8392 126.197 52.6445 126.122 52.4632C126.046 52.2819 125.935 52.1177 125.795 51.9807C125.654 51.8436 125.488 51.7365 125.305 51.6657C125.123 51.5949 124.928 51.5619 124.732 51.5686C124.34 51.5685 123.964 51.7218 123.684 51.9958C123.403 52.2698 123.24 52.643 123.23 53.0357V53.0357Z" fill="black"></path>
            <path d="M132.972 50.6341V55.8694H131.971V49.0465H133.023L136.727 54.1611V49.0465H137.729V55.8694H136.727L132.972 50.6341Z" fill="black"></path>
            <path d="M141.884 50.805C142.222 50.8059 142.555 50.8779 142.863 51.0163C143.171 51.1546 143.447 51.3562 143.672 51.6081C143.898 51.86 144.068 52.1565 144.172 52.4786C144.276 52.8006 144.312 53.1411 144.277 53.4778V53.709H140.382C140.4 54.1372 140.586 54.541 140.899 54.8326C141.212 55.1242 141.627 55.2801 142.054 55.2665C142.599 55.2704 143.123 55.0613 143.516 54.6837L143.947 55.2866C143.679 55.5371 143.363 55.7319 143.02 55.8595C142.676 55.9871 142.31 56.0451 141.944 56.0301C141.284 56.0005 140.66 55.7191 140.2 55.2434C139.739 54.7677 139.477 54.1335 139.467 53.4705C139.457 52.8074 139.699 52.1654 140.145 51.6757C140.59 51.1859 141.205 50.8852 141.864 50.8351L141.884 50.805ZM140.372 53.0357H143.376C143.378 52.8392 143.339 52.6445 143.264 52.4632C143.188 52.2819 143.077 52.1177 142.937 51.9807C142.797 51.8436 142.63 51.7365 142.447 51.6657C142.265 51.5949 142.07 51.5619 141.874 51.5686C141.483 51.5685 141.107 51.7218 140.826 51.9958C140.545 52.2698 140.383 52.643 140.372 53.0357V53.0357Z" fill="black"></path>
            <path d="M146.129 54.7841V51.7696H145.308V50.9657H146.129V49.579H147.041V50.9356H148.042V51.7395H147.041V54.5731C147.041 54.9248 147.211 55.176 147.521 55.176C147.706 55.1793 147.886 55.1109 148.022 54.9851L148.252 55.6784C148.123 55.793 147.972 55.8805 147.809 55.9357C147.646 55.991 147.473 56.0128 147.301 56C147.139 56.0167 146.976 55.9964 146.824 55.9406C146.671 55.8848 146.533 55.795 146.421 55.6779C146.308 55.5608 146.223 55.4195 146.172 55.2647C146.122 55.1098 146.107 54.9455 146.129 54.7841V54.7841Z" fill="black"></path>
            <path d="M152.628 52.0811L151.416 55.8694H150.465L148.923 50.9356H149.924L151.046 54.6937L152.197 50.9356H152.988L154.22 54.6937L155.341 50.9356H156.343L154.791 55.8694H153.789L152.628 52.0811Z" fill="black"></path>
            <path d="M157.364 53.3975C157.384 52.9069 157.547 52.433 157.833 52.0348C158.119 51.6366 158.515 51.3316 158.973 51.1578C159.43 50.9841 159.929 50.9493 160.406 51.0577C160.883 51.1661 161.318 51.4129 161.656 51.7675C161.994 52.1221 162.221 52.5687 162.309 53.0518C162.396 53.5348 162.339 54.033 162.147 54.4842C161.954 54.9353 161.633 55.3196 161.224 55.5892C160.815 55.8587 160.336 56.0016 159.847 56C159.51 56.0062 159.175 55.9423 158.864 55.8122C158.552 55.6821 158.271 55.4888 158.038 55.2443C157.805 54.9998 157.624 54.7095 157.508 54.3917C157.392 54.0738 157.343 53.7353 157.364 53.3975V53.3975ZM161.369 53.3975C161.327 53.1017 161.201 52.8244 161.006 52.5986C160.811 52.3728 160.555 52.208 160.27 52.1239C159.984 52.0399 159.68 52.0401 159.394 52.1245C159.109 52.2089 158.853 52.3739 158.659 52.6C158.464 52.826 158.338 53.1035 158.296 53.3993C158.254 53.6951 158.298 53.9968 158.422 54.2684C158.547 54.5399 158.746 54.7699 158.997 54.9308C159.248 55.0917 159.539 55.1769 159.837 55.176C160.058 55.1705 160.275 55.1189 160.475 55.0244C160.675 54.93 160.854 54.7948 160.999 54.6275C161.144 54.4602 161.252 54.2645 161.318 54.0527C161.383 53.8409 161.404 53.6178 161.379 53.3975H161.369Z" fill="black"></path>
            <path d="M164.013 50.9356H164.924V51.6892C165.109 51.4309 165.351 51.218 165.63 51.0666C165.909 50.9152 166.219 50.8292 166.536 50.815V51.7495C166.433 51.7381 166.329 51.7381 166.226 51.7495C165.97 51.7677 165.722 51.8408 165.497 51.9639C165.273 52.087 165.077 52.2572 164.924 52.4629V55.8694H164.013V50.9356Z" fill="black"></path>
            <path d="M169.63 53.7492L168.879 54.5028V55.8694H167.968V49.0465H168.879V53.4376L171.222 50.9356H172.384L170.261 53.1764L172.404 55.8694H171.222L169.63 53.7492Z" fill="black"></path>
            <path d="M69.4501 28.0151C69.3977 26.1032 69.7343 24.2007 70.4393 22.4238C71.1443 20.6468 72.203 19.0329 73.5507 17.6804C74.8983 16.328 76.5066 15.2656 78.2772 14.5581C80.0479 13.8505 81.9437 13.5128 83.8488 13.5654C86.0536 13.4839 88.2412 13.9826 90.1945 15.0121C92.1477 16.0415 93.7986 17.5659 94.9832 19.4337C95.0166 19.4857 95.0386 19.544 95.0481 19.6051C95.0575 19.6661 95.0541 19.7285 95.0381 19.7881C95.0221 19.8478 94.9937 19.9034 94.955 19.9514C94.9162 19.9994 94.8679 20.0387 94.813 20.0668L91.4386 21.7951C91.3425 21.8441 91.232 21.8568 91.1273 21.831C91.0226 21.8052 90.9305 21.7425 90.8679 21.6544C90.099 20.498 89.0562 19.5511 87.8329 18.8987C86.6096 18.2463 85.2441 17.9088 83.8588 17.9164C78.5619 17.9164 74.4666 22.1468 74.4666 28.0151C74.4666 33.8834 78.5619 38.1138 83.8588 38.1138C85.2443 38.1288 86.6116 37.7956 87.8358 37.1444C89.0601 36.4932 90.1025 35.5449 90.8679 34.3858C90.9276 34.2943 91.019 34.2282 91.1244 34.2004C91.2298 34.1725 91.3418 34.1849 91.4386 34.2351L94.803 35.9634C94.859 35.9903 94.9085 36.0289 94.9482 36.0768C94.988 36.1246 95.0169 36.1806 95.033 36.2407C95.0492 36.3008 95.0522 36.3637 95.0419 36.4251C95.0316 36.4865 95.0082 36.545 94.9732 36.5964C93.788 38.4614 92.1384 39.9836 90.1874 41.0128C88.2364 42.042 86.0516 42.5425 83.8488 42.4647C81.9437 42.5174 80.0479 42.1796 78.2772 41.4721C76.5066 40.7645 74.8983 39.7021 73.5507 38.3497C72.203 36.9973 71.1443 35.3833 70.4393 33.6064C69.7343 31.8295 69.3977 29.9269 69.4501 28.0151V28.0151Z" fill="black"></path>
            <path d="M111.364 39.7918C110.529 40.6757 109.516 41.3712 108.392 41.832C107.268 42.2928 106.059 42.5085 104.846 42.4647C101.552 42.4647 97.8369 40.244 97.8369 35.7624C97.8369 31.12 101.552 29.1907 104.846 29.1907C106.031 29.1272 107.215 29.3165 108.321 29.746C109.428 30.1756 110.431 30.8357 111.264 31.6828C111.27 31.691 111.277 31.6978 111.286 31.7026C111.295 31.7073 111.304 31.7097 111.314 31.7097C111.324 31.7097 111.334 31.7073 111.343 31.7026C111.352 31.6978 111.359 31.691 111.364 31.6828V28.7687C111.364 26.4274 109.442 25.0005 106.638 25.0005C104.518 24.9948 102.479 25.8163 100.951 27.2916C100.917 27.3218 100.877 27.3442 100.833 27.3571C100.79 27.3701 100.744 27.3733 100.699 27.3666C100.654 27.3599 100.612 27.3434 100.574 27.3183C100.536 27.2932 100.504 27.2601 100.48 27.2212L99.0084 24.7091C98.9711 24.6496 98.9556 24.5789 98.9648 24.5091C98.9739 24.4394 99.0071 24.3751 99.0585 24.3273C101.329 22.2752 104.294 21.1685 107.349 21.2324C111.815 21.2324 115.74 23.1114 115.74 28.5577V41.691C115.74 41.763 115.712 41.832 115.661 41.8829C115.61 41.9337 115.541 41.9623 115.47 41.9623H111.635C111.563 41.9623 111.494 41.9337 111.444 41.8829C111.393 41.832 111.364 41.763 111.364 41.691V39.7918ZM111.364 35.3705C111.367 35.1988 111.326 35.0293 111.244 34.8782C110.76 34.0643 110.074 33.3903 109.252 32.9219C108.431 32.4535 107.503 32.2066 106.558 32.2053C104.095 32.2053 102.263 33.6322 102.263 35.8529C102.263 38.0736 104.095 39.4502 106.558 39.4502C107.427 39.4741 108.291 39.3066 109.088 38.9593C109.886 38.6121 110.598 38.0937 111.174 37.4405C111.297 37.256 111.363 37.0394 111.364 36.8175V35.3705Z" fill="black"></path>
            <path d="M122.299 37.2697V25.5833H119.295C119.217 25.5834 119.142 25.553 119.086 25.4986C119.03 25.4443 118.997 25.3701 118.994 25.2919V22.0262C118.997 21.948 119.03 21.8739 119.086 21.8195C119.142 21.7651 119.217 21.7347 119.295 21.7348H122.299V16.4493C122.299 16.3844 122.324 16.322 122.368 16.2751C122.413 16.2283 122.474 16.2007 122.539 16.1981H126.434C126.5 16.1981 126.564 16.2246 126.611 16.2717C126.658 16.3188 126.684 16.3827 126.684 16.4493V21.7348H130.479C130.556 21.7348 130.63 21.7655 130.685 21.8201C130.739 21.8748 130.77 21.9489 130.77 22.0262V25.2919C130.77 25.3692 130.739 25.4433 130.685 25.498C130.63 25.5526 130.556 25.5833 130.479 25.5833H126.684V36.1744C126.684 37.5611 127.345 38.5659 128.597 38.5659C129.225 38.5739 129.84 38.3879 130.359 38.0334C130.382 38.0137 130.41 38.0002 130.44 37.9942C130.469 37.9881 130.5 37.9896 130.529 37.9986C130.558 38.0077 130.584 38.0239 130.605 38.0457C130.626 38.0676 130.642 38.0945 130.65 38.1238L131.571 41.0479C131.58 41.0813 131.581 41.1167 131.572 41.1504C131.563 41.184 131.546 41.2146 131.521 41.2388C130.379 42.1226 128.954 42.5552 127.515 42.4547C124.091 42.4647 122.299 40.6259 122.299 37.2697Z" fill="black"></path>
            <path d="M143.706 21.2324C149.714 21.2324 153.649 25.8748 153.649 32.2857V32.9891C153.647 33.0806 153.609 33.1675 153.543 33.2313C153.478 33.2951 153.39 33.3307 153.299 33.3307H138.279C138.255 33.3319 138.231 33.3382 138.208 33.3491C138.186 33.36 138.167 33.3754 138.151 33.3942C138.135 33.413 138.123 33.435 138.116 33.4586C138.109 33.4822 138.106 33.5071 138.109 33.5317C138.297 35.0769 139.064 36.4923 140.254 37.4907C141.445 38.4891 142.969 38.9957 144.517 38.9076C146.544 38.9138 148.509 38.2022 150.065 36.8979C150.083 36.8765 150.106 36.8594 150.132 36.8477C150.158 36.8359 150.186 36.8299 150.215 36.8299C150.243 36.8299 150.271 36.8359 150.297 36.8477C150.323 36.8594 150.346 36.8765 150.365 36.8979L152.137 39.4502C152.169 39.4882 152.186 39.5362 152.186 39.5859C152.186 39.6355 152.169 39.6835 152.137 39.7215C149.905 41.6181 147.051 42.6169 144.127 42.525C142.72 42.5867 141.315 42.3533 140.003 41.8398C138.691 41.3263 137.5 40.5439 136.506 39.5426C135.512 38.5414 134.737 37.3431 134.23 36.0244C133.724 34.7057 133.496 33.2955 133.563 31.8837C133.483 25.9652 137.739 21.2324 143.706 21.2324ZM138.319 30.1956H149.113C149.148 30.1956 149.183 30.1885 149.215 30.1748C149.248 30.161 149.277 30.1409 149.301 30.1155C149.325 30.0902 149.344 30.0602 149.357 30.0273C149.369 29.9945 149.375 29.9594 149.374 29.9243C149.294 28.4952 148.652 27.1563 147.589 26.2012C146.527 25.2462 145.131 24.753 143.706 24.8297C142.3 24.7965 140.933 25.3029 139.886 26.2459C138.838 27.1888 138.188 28.4971 138.069 29.9042C138.063 29.9404 138.065 29.9774 138.075 30.0127C138.085 30.048 138.103 30.0806 138.126 30.1084C138.15 30.1362 138.18 30.1584 138.213 30.1734C138.247 30.1885 138.283 30.1961 138.319 30.1956V30.1956Z" fill="black"></path>
            <path d="M171.593 29.3113C171.593 26.1762 170.01 25.1211 167.587 25.1211C166.596 25.1473 165.624 25.3966 164.742 25.8505C163.86 26.3045 163.09 26.9515 162.491 27.7438C162.441 27.8106 162.413 27.8914 162.411 27.9749V41.7212C162.411 41.7851 162.385 41.8465 162.34 41.8917C162.295 41.9369 162.234 41.9623 162.17 41.9623H158.205C158.141 41.9623 158.08 41.9369 158.035 41.8917C157.99 41.8465 157.965 41.7851 157.965 41.7212V21.976C157.965 21.912 157.99 21.8507 158.035 21.8054C158.08 21.7602 158.141 21.7348 158.205 21.7348H162.11C162.142 21.7348 162.173 21.741 162.202 21.7532C162.231 21.7653 162.258 21.783 162.28 21.8054C162.302 21.8278 162.32 21.8544 162.332 21.8837C162.344 21.9129 162.351 21.9443 162.351 21.976V24.3373C162.355 24.3481 162.363 24.3573 162.373 24.3637C162.382 24.3701 162.394 24.3735 162.406 24.3735C162.417 24.3735 162.429 24.3701 162.439 24.3637C162.448 24.3573 162.456 24.3481 162.461 24.3373C163.348 23.3541 164.429 22.5667 165.636 22.0253C166.842 21.4838 168.148 21.2001 169.47 21.1922C173.805 21.1922 175.978 23.5335 175.978 27.6835V41.7212C175.978 41.7851 175.953 41.8465 175.908 41.8917C175.863 41.9369 175.802 41.9623 175.738 41.9623H171.843C171.81 41.9637 171.778 41.9584 171.748 41.9469C171.717 41.9354 171.69 41.9179 171.666 41.8953C171.643 41.8728 171.624 41.8458 171.612 41.8158C171.599 41.7859 171.592 41.7537 171.593 41.7212V29.3113Z" fill="black"></path>
            <path d="M202.933 29.9444H211.945C212.073 29.9443 212.197 29.9948 212.288 30.085C212.38 30.1752 212.433 30.2979 212.436 30.4267V33.2302C212.433 33.3591 212.38 33.4817 212.288 33.5719C212.197 33.6621 212.073 33.7126 211.945 33.7125H202.933C202.805 33.7126 202.682 33.6621 202.59 33.5719C202.498 33.4817 202.445 33.3591 202.443 33.2302V30.4267C202.445 30.2979 202.498 30.1752 202.59 30.085C202.682 29.9948 202.805 29.9443 202.933 29.9444Z" fill="black"></path>
            <path d="M225.502 31.1502L217.893 41.8216C217.865 41.8559 217.829 41.8836 217.79 41.9027C217.75 41.9218 217.706 41.9319 217.662 41.9322H212.506C212.455 41.9326 212.406 41.9191 212.363 41.893C212.32 41.8669 212.285 41.8293 212.262 41.7845C212.239 41.7396 212.229 41.6892 212.234 41.639C212.238 41.5888 212.255 41.5407 212.285 41.5001L222.298 27.6835C222.317 27.663 222.328 27.6361 222.328 27.6081C222.328 27.5801 222.317 27.5532 222.298 27.5328L212.866 14.4698C212.836 14.4292 212.818 14.3811 212.814 14.3309C212.81 14.2806 212.82 14.2303 212.843 14.1854C212.866 14.1405 212.901 14.103 212.944 14.0769C212.987 14.0508 213.036 14.0372 213.086 14.0377H218.283C218.326 14.0381 218.368 14.0482 218.406 14.0674C218.444 14.0865 218.477 14.1142 218.503 14.1482L225.512 24.1967C225.526 24.2134 225.542 24.2269 225.561 24.2362C225.58 24.2455 225.601 24.2503 225.623 24.2503C225.644 24.2503 225.665 24.2455 225.684 24.2362C225.703 24.2269 225.72 24.2134 225.733 24.1967L232.682 14.1985C232.707 14.1638 232.74 14.1358 232.779 14.1166C232.817 14.0973 232.859 14.0875 232.902 14.0879H238.109C238.159 14.0875 238.208 14.101 238.251 14.1271C238.294 14.1532 238.329 14.1908 238.352 14.2356C238.375 14.2805 238.385 14.3309 238.381 14.3811C238.377 14.4313 238.359 14.4794 238.329 14.52L228.937 27.583C228.918 27.6035 228.907 27.6304 228.907 27.6584C228.907 27.6863 228.918 27.7133 228.937 27.7337L238.95 41.5905C238.98 41.6311 238.997 41.6792 239.002 41.7294C239.006 41.7797 238.996 41.83 238.973 41.8749C238.95 41.9197 238.915 41.9573 238.872 41.9834C238.829 42.0095 238.78 42.0231 238.73 42.0226H233.573C233.528 42.0238 233.484 42.0144 233.444 41.9952C233.404 41.976 233.369 41.9475 233.343 41.9121L225.713 31.1502C225.701 31.1336 225.685 31.1201 225.666 31.1108C225.648 31.1014 225.628 31.0966 225.608 31.0966C225.587 31.0966 225.567 31.1014 225.549 31.1108C225.53 31.1201 225.514 31.1336 225.502 31.1502V31.1502Z" fill="black"></path>
            <path d="M194.012 39.7617C193.18 40.6487 192.169 41.347 191.047 41.8097C189.924 42.2725 188.716 42.4889 187.503 42.4446C184.199 42.4446 180.494 40.2239 180.494 35.7423C180.494 31.0999 184.209 29.1706 187.503 29.1706C188.711 29.1022 189.918 29.2986 191.042 29.7464C192.166 30.1941 193.18 30.8823 194.012 31.7631V28.7486C194.012 26.4073 192.089 24.9804 189.296 24.9804C187.172 24.9742 185.13 25.7956 183.598 27.2715C183.564 27.3017 183.524 27.3241 183.481 27.337C183.437 27.35 183.391 27.3532 183.347 27.3465C183.302 27.3398 183.259 27.3233 183.221 27.2982C183.183 27.2731 183.151 27.24 183.128 27.2012L181.656 24.689C181.621 24.6279 181.609 24.5569 181.62 24.4876C181.63 24.4183 181.664 24.3548 181.716 24.3072C183.987 22.2551 186.951 21.1484 190.006 21.2123C194.472 21.2123 198.397 23.0913 198.397 28.5376V41.6709C198.397 41.7429 198.369 41.8119 198.318 41.8628C198.267 41.9136 198.199 41.9422 198.127 41.9422H194.252C194.18 41.9422 194.112 41.9136 194.061 41.8628C194.01 41.8119 193.982 41.7429 193.982 41.6709L194.012 39.7617ZM194.012 35.3504C194.011 35.1758 193.97 35.0037 193.892 34.848C193.415 34.0349 192.736 33.3596 191.922 32.8877C191.108 32.4158 190.186 32.1634 189.245 32.155C186.782 32.155 184.94 33.5819 184.94 35.8026C184.94 38.0233 186.782 39.4 189.245 39.4C190.113 39.4238 190.975 39.2561 191.771 38.9089C192.567 38.5616 193.277 38.0432 193.851 37.3903C193.978 37.2077 194.045 36.9898 194.042 36.7673L194.012 35.3504Z" fill="black"></path>
        </g>
        <defs>
            <clipPath id="clip0_1185_4388">
            <rect width="239" height="56" fill="white"></rect>
            </clipPath>
        </defs>
        </symbol>
    </svg>
    <svg class="icon d-block d-xl-none">
      <use xlink:href="#logo-head-xxl"></use>
    </svg>
    <h1>
      <svg width="24" height="24" xmlns="http://www.w3.org/2000/svg" fill-rule="evenodd" clip-rule="evenodd"><path d="M22 3c.53 0 1.039.211 1.414.586s.586.884.586 1.414v14c0 .53-.211 1.039-.586 1.414s-.884.586-1.414.586h-20c-.53 0-1.039-.211-1.414-.586s-.586-.884-.586-1.414v-14c0-.53.211-1.039.586-1.414s.884-.586 1.414-.586h20zm1 8h-22v8c0 .552.448 1 1 1h20c.552 0 1-.448 1-1v-8zm-15 5v1h-5v-1h5zm13-2v1h-3v-1h3zm-10 0v1h-8v-1h8zm-10-6v2h22v-2h-22zm22-1v-2c0-.552-.448-1-1-1h-20c-.552 0-1 .448-1 1v2h22z"/></svg>
      Managed Identity Wallets
    </h1>
    <p>The Managed Identity Wallets service implements the Self-Sovereign-Identity (SSI) readiness by providing a wallet hosting platform including a DID resolver, service endpoints and the company wallets itself.</p>
    <p>&gt; <a href="/docs">API documentation</a></p>
    <p>&gt; <a href="/ui/">Admin UI</a></p>
    <p>&gt; <a href="https://github.com/catenax-ng/product-core-managed-identity-wallets">Code repository</a></p>
  </body>
</html>
""", ContentType.Text.Html)
        }

        authenticate("auth-ui") {
            get("/login") {
                // triggers automatically the login
            }
            route("/callback") {
                // This handler will be executed after making a request to a provider's token URL.
                handle {
                    val principal = call.authentication.principal<OAuthAccessTokenResponse>()
                    if (principal != null) {
                        val response = principal as OAuthAccessTokenResponse.OAuth2
                        // put token directly into the session, without decoding it
                        call.sessions.set(UserSession(response.accessToken.toString()))
                        call.respondRedirect("/ui/")
                    } else {
                        call.respondRedirect("/login")
                    }
                }
            }
        }

        route("/ui") {

            authenticate("auth-ui-session") {
                static("/") {
                    staticRootFolder = File("/app/static")
                    files(".")
                    default("index.html")
                }

                route("/wallets") {
                    handle {

                        val userSession = call.sessions.get<UserSession>()
                        if (userSession != null) {
                            // could be used later
                        }

                        call.respond(walletService.getAll())
                    }
                }

                route("/wallets/{did}/full") {
                    handle {
                        val userSession = call.sessions.get<UserSession>()
                        var token = ""
                        if (userSession != null) {
                            token = userSession.token
                        }

                        val did = call.parameters["did"]
                        if (did != null) {
                            try {
                                val stringBody = retrieveBusinessPartnerInfo("${datapoolUrl}", did, token)
                                Json { ignoreUnknownKeys = true }.decodeFromString<BusinessPartnerInfo>(
                                    BusinessPartnerInfo.serializer(),
                                    stringBody
                                )
                                call.respondText(stringBody, ContentType.Application.Json, HttpStatusCode.OK)
                            } catch (e: RedirectResponseException) {
                                log.warn("RedirectResponseException: " + e.message)
                                throw BadRequestException("Could not retrieve business partner details!")
                            } catch (e: ClientRequestException) {
                                log.warn("ClientRequestException: " + e.message)
                                throw BadRequestException("Could not retrieve business partner details!")
                            } catch (e: ServerResponseException) {
                                log.warn("ServerResponseException: " + e.message)
                                throw BadRequestException("Could not retrieve business partner details!")
                            } catch (e: SerializationException) {
                                log.warn("SerializationException: " + e.message)
                                throw BadRequestException(e.message)
                            } catch (e: IOException) {
                                log.warn("IOException: $datapoolUrl " + e.message)
                                throw BadRequestException(e.message)
                            }
                        } else {
                            throw BadRequestException("No DID given!")
                        }
                    }
                }
            }
        }
    }

}