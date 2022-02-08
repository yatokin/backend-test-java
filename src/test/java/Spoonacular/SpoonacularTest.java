package Spoonacular;

import java.io.IOException;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

class SpoonaccularTest extends AbstractTest {

    private static String API_KEY = "d38bdc89b3574dc9925c962197fc03d7";

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "https://api.spoonacular.com";
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
                .log()
                .body()
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


}
