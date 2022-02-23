package Spoonacular;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.stream.Stream;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

class SpoonaccularTest extends AbstractTest {

    private static String API_KEY = "d38bdc89b3574dc9925c962197fc03d7";
    private static RequestSpecification BASE_SPEC;
    private static ResponseSpecification RESPONSE_SPEC;

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "https://api.spoonacular.com";

        BASE_SPEC = new RequestSpecBuilder()
                .addQueryParam("apiKey", API_KEY)
                .log(LogDetail.ALL)
                .build();

        RESPONSE_SPEC = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .log(LogDetail.ALL)
                .build();
    }

    private static Stream<Arguments> testImageClassificationData() {
        return Stream.of(
                Arguments.of("steak", "steak.jpg"),
                Arguments.of("pasta", "pasta.jpg")
        );
    }

    @Test
    void testGetRecipesComplexSearch() throws IOException, JSONException {

        String actual = given()
                .param("apiKey", API_KEY)
                .param("number", 10)
                .log()
                .parameters()
                .expect()
                .statusCode(200)
                .time(Matchers.lessThan(3000L))
                .body("offset", is(0))
                .body("number", is(10))
                .log()
                .body()
                .when()
                .get("recipes/complexSearch")
                .body()
                .asPrettyString();

        System.out.println(actual);
    }

    @Test
    void testGetRecipesComplexSearchSteak4() throws IOException, JSONException {

        String actual1 = given()
                .param("apiKey", API_KEY)
                .param("query", "steak")
                .param("number", 4)
                .log()
                .parameters()
                .expect()
                .statusCode(200)
                .time(Matchers.lessThan(5000L))
                .body("offset", is(0))
                .body("number", is(4))
                .spec(RESPONSE_SPEC)
                .when()
                .get("recipes/complexSearch")
                .body()
                .asPrettyString();

        String expected = getResourceAsString("expected.json");

        JSONAssert.assertEquals(
                expected,
                actual1,
                JSONCompareMode.NON_EXTENSIBLE
        );

    }

    @Test
    void testGetRecipesComplexSearchPastaVegetarian() throws IOException, JSONException {

        String actual2 = given()
                .param("apiKey", API_KEY)
                .param("query", "pasta")
                .param("diet", "vegetarian")
                .param("number", 3)
                .log()
                .parameters()
                .expect()
                .statusCode(200)
                .time(Matchers.lessThan(3000L))
                .body("offset", is(0))
                .body("number", is(3))
                .log()
                .body()
                .when()
                .get("recipes/complexSearch")
                .body()
                .asPrettyString();

        System.out.println(actual2);
    }

    @ParameterizedTest
    @MethodSource("testImageClassificationData")
    void testImageClassification(String dir, String resource) throws IOException {

        String separator = FileSystems.getDefault().getSeparator();
        File file = getFile("images" + separator + dir + separator + resource);

        ImageClassifierResponse response = given()
                .spec(BASE_SPEC)
                .multiPart("file", file)
                .expect()
                .body("status", is("success"))
                .body("category", is(dir))
                .body("probability", Matchers.greaterThan(0.9f))
                .spec(RESPONSE_SPEC)
                .when()
                .post("food/images/classify")
                .as(ImageClassifierResponse.class);

        ImageClassifierResponse expected = ImageClassifierResponse.builder()
                .status("success")
                .category(dir)
                .build();

        Assertions.assertEquals(expected.getStatus(), response.getStatus());

    }


}

