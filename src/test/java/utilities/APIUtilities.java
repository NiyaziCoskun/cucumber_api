package utilities;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.junit.Assert;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static stepDefinitions.Hooks.*;

public class APIUtilities {
    public static Response response;
    public static RequestSpecification specification = new RequestSpecBuilder().
            addCookie(new Cookie.Builder("PHPSESSID", userSessionID).build()).
            setBaseUri(ConfigReader.getProperty("baseURI")).setRelaxedHTTPSValidation().build();

    static Map<String, Object> payload = new HashMap<>();

    public static int addCoupon(String promoCode, int startDate, int endDate, int usersLimit, int discountRate, String discountType, int category) {
        payload.put("promoCode", promoCode);
        payload.put("startedAt", BrowserUtilities.getDay_day_month_year_time(startDate));
        payload.put("enddedAt", BrowserUtilities.getDay_day_month_year_time(endDate));
        payload.put("usersLimit", usersLimit);
        payload.put("discountRate", discountRate);
        payload.put("discountType", discountType);
        payload.put("category", category);

        response = given()
                .spec(specification)
                .contentType(ContentType.JSON)
                .headers("content-type", "application/x-www-form-urlencoded")
                .formParams(payload)
                .post("/promoCode/add");

        response.prettyPrint();
        return response.jsonPath().get("promoCode.id");
    }

    public static void deleteCoupon(int couponId) {
        payload.put("couponId", couponId);

        response = given()
                .spec(specification)
                .headers("content-type", "application/x-www-form-urlencoded")
                .formParams(payload)
                .post("/promoCode/deleteCoupon");

        response.prettyPrint();
    }

    public static List<Integer> getCoupons() {
        response = given()
                .spec(specification)
                .headers("content-type", "application/x-www-form-urlencoded")
                .post("/promoCode/getCoupons");

        response.prettyPrint();
        return response.jsonPath().getList("id");
    }

    public static int createTimeoff(String specificDate, String startAt, String finishAt, String title, boolean isAll) {
        payload.put("specificDate", specificDate);
        payload.put("startAt", startAt);
        payload.put("finishAt", finishAt);
        payload.put("title", title);
        payload.put("isAll", isAll);

        response = given()
                .spec(specification)
                .headers("content-type", "application/x-www-form-urlencoded")
                .formParams(payload)
                .post("/hypnotherapist/timeoff/create");

        response.prettyPrint();
        return response.jsonPath().getInt("data[0].id");
    }


    //Cookie ekleme
    public static void addCookie(String key, String value) {
        specification.cookie(key, value);
    }

    //Cookie'ler ekleme
    public static void addCookies(Map<String, String> map) {
        specification.cookies(map);
    }

    // Status code do??rulamas?? yapar
    public static void verifyStatusCode(int statusCode) {
        Assert.assertEquals(statusCode, response.getStatusCode());
    }

    // Ba??lant?? i??in kabul edilen content-type ifadesini ekler
    public static void addContentType(String contentType) {
        specification.contentType(contentType);
    }

    // Ba??lant??ya query param ekler
    public static void addQueryParam(String key, String value) {
        specification.queryParam(key, value);
    }

    // Ba??lant??ya query params ekler
    public static void addQueryParams(Map<String, String> params) {
        specification.queryParams(params);
    }

    // Ba??lant??ya form param ekler
    public static void addFormParam(String key, String value) {
        specification.formParam(key, value);
    }

    // Ba??lant??ya form params ekler
    public static void addFormParams(Map<String, String> params) {
        specification.formParams(params);
    }

    // Ba??lant??ya body ekler
    public static void addBody(String s) {
        specification.body(s);
    }

    // Kullan??c?? istedi??i endpoint'e post tipinde ve dalar?? form params i??erisinde g??ndererek ba??lanabilecek
    public static void connectWithPostMethodFormParams(String endPoint, Map<String, String> data) {
        response = given().
                contentType(ContentType.URLENC.withCharset("UTF-8")).
                spec(specification).
                formParams(data).
                post(endPoint);
    }

    // Kullan??c?? istedi??i endpoint'e post tipinde ve dalar?? body b??l??m??nde g??ndererek ba??lanabilecek
    public static void connectWithPostMethodBody(String endPoint, Map<String, String> data) {
        JSONObject object = new JSONObject();

        for (String s : data.keySet()) {
            object.put(s, data.get(s));
        }

        response = given().
                contentType(ContentType.URLENC.withCharset("UTF-8")).
                spec(specification).
                body(data.toString()).
                post(endPoint)
        ;
    }

    // Kullan??c?? istedi??i endpoint'e post tipinde ba??lanabilecek
    public static void connectWithPostMethod(String endPoint) {
        response = given().
                contentType(ContentType.URLENC.withCharset("UTF-8")).
                spec(specification).
                post(endPoint);

        response.prettyPrint();
    }

    // Kullan??c?? istedi??i endpoint'e get tipinde ba??lanabilecek
    public static void connectWithGetMethod(String endPoint) {
        response = given().
                contentType(ContentType.URLENC.withCharset("UTF-8")).
                spec(specification).
                get(endPoint);
    }

    // Kullan??c?? istedi??i endpoint'e delete tipinde ba??lanabilecek
    public static void connectWithDeleteMethod(String endPoint) {
        response = given().
                contentType(ContentType.URLENC.withCharset("UTF-8")).
                spec(specification).
                delete(endPoint);
    }

    // Kullan??c?? istedi??i endpoint'e delete tipinde ba??lanabilecek
    public static void connectWithDeleteMethod(String endPoint, Map<String, String> queryMap) {
        response = given().
                queryParams(queryMap).
                contentType(ContentType.URLENC.withCharset("UTF-8")).
                spec(specification).
                delete(endPoint);
    }

    // Kullan??c?? istedi??i endpoint'e get tipinde query verilerini g??ndererek ba??lanabilecek
    public static void connectWithGetMethodQuery(String endPoint, Map<String, String> queryMap) {
        response = given().
                queryParams(queryMap).
                contentType(ContentType.URLENC.withCharset("UTF-8")).
                spec(specification).
                get(endPoint);
    }

    // Gelen response i??erisindeki array'e ait verilerin sahip oldu??u field'lar?? kntrol eder
    public static void checkFieldsInArray(List<String> fields) {

        List<LinkedHashMap> list = APIUtilities.response.jsonPath().get("$");

        for (LinkedHashMap o : list) {

            for (String field : fields) {
                Assert.assertTrue(o.get(field) != null);
            }
        }
    }

    // Gelen response i??erisindeki objelerin field'lar?? kntrol eder
    public static void checkFieldsInObject(List<String> fields) {
        for (String field : fields) {
            Assert.assertTrue(APIUtilities.response.jsonPath().get(field) != null);
        }
    }

}
