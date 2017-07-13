package test;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
//import static org.hamcrest.Matchers.*;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class SnakeAndLadderTestv3 {
	Response response = null;
	String jsonString = null;
	String id;
	String id2;
	String accessToken;

	/**
	 * Setup basic path
	 */
		
		@BeforeTest
	 	public void authorisation() {
			Response response = given().parameters("username", "su", "password", "root_pass", "grant_type", "client_credentials", "client_id",
	 						"213e199278515abd0f1ae0de89bc0387ed86b3ba30a5908eeb45f2418dac6dd9", "client_secret",
	 					"a0c979ffb7614392d04cbbf7bd2a9b9bb7bb871e6fc4210cf09cfb217d390cc5")
	 				.auth().preemptive()
	 				.basic("213e199278515abd0f1ae0de89bc0387ed86b3ba30a5908eeb45f2418dac6dd9",
	 						"a0c979ffb7614392d04cbbf7bd2a9b9bb7bb871e6fc4210cf09cfb217d390cc5")
	 				.when().post("http://10.0.1.86/snl/oauth/token");
	 
	 		JsonPath jsonPath = new JsonPath(response.asString());
	 		accessToken = jsonPath.getString("access_token");
	 		System.out.println(accessToken);
	 		RestAssured.baseURI = "http://10.0.1.86/snl/";
			RestAssured.basePath = "/rest/v3/";
	}
	/**
	 * test response for new board
	 * @throws ParseException 
	 */
	@Test(priority = 1)
	public void test_new_board() throws ParseException {
		response =  given().auth().oauth2(accessToken).when().get("/board.json");
		Assert.assertEquals(response.statusCode(), 200);
		response = given().auth().oauth2(accessToken).when().get("/board/new.json");
		jsonString = response.asString();
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
		JSONObject msg = (JSONObject) jsonObject.get("response");
		JSONObject msg2 = (JSONObject) msg.get("board");
		id = msg2.get("id").toString();
	System.out.println(id);
	}

	/**
	 * test Get method response
	 */
	@Test(priority = 2)
	public void test_methods_for_board_list() {

		response =  given().auth().oauth2(accessToken).when().get("/board.json");
		Assert.assertEquals(response.statusCode(), 200);
		response =  given().auth().oauth2(accessToken).when().get("/board.json").then().contentType(ContentType.JSON).extract().response();
		String jsonAsString2 = response.asString();
		// System.out.println(response.asString());
		Assert.assertFalse(jsonString.contains(jsonAsString2));

	}

	/**
	 * Test of joining new player to board
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	
	@Test(priority = 3)
	public void test_new_player() throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();	
		Object obj = parser.parse(new FileReader("C:\\Users\\sumitmishra.QAIT\\workspace\\assignment4\\src\\resource\\dat.json"));
		JSONObject jsonObject = (JSONObject) obj;
		jsonObject.put("board", id);
			
	response =	 given().auth().oauth2(accessToken).when().contentType("application/json").body(jsonObject).when().post("/player.json");
	Assert.assertEquals(response.statusCode(), 200);
	 id2=(response.getBody().jsonPath().getJsonObject("response.player.id")).toString();
	}

	/**
	 * check response for board with specific id
	 */
	@Test(priority = 6)
	public void test_board_with_id() {
		response = given().auth().oauth2(accessToken).when().get("/board/"+id+".json");
		Assert.assertEquals(response.statusCode(), 200);
		response = given().put("/board/7.json").andReturn();
		 
	}


	/**
	 * tests for service call on player
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(priority = 5)
	public void test_player_at_id() throws ParseException, FileNotFoundException, IOException {
		response=given().auth().oauth2(accessToken).when().get("/player/"+id2+".json");
		Assert.assertEquals(response.statusCode(), 200);
		JSONParser parser = new JSONParser();	
		Object obj = parser.parse(new FileReader("C:\\Users\\sumitmishra.QAIT\\workspace\\assignment4\\src\\resource\\db.json"));
		JSONObject jsonObject = (JSONObject) obj;
		response= given().auth().oauth2(accessToken).contentType("application/json").body(jsonObject).when().put("/player/"+id2+".json");
		Assert.assertEquals(response.statusCode(), 200);
		 response=given().delete("/player/"+id2+".json");

	}
	/**
	 * Moving player with id
	 */
	@Test(priority=4)
	public void test_move_player(){
		response= given().auth().oauth2(accessToken).when().get("/move/"+id+".json?player_id="+id2);
		Assert.assertEquals(response.statusCode(), 200);
		
	}
}
	