package test;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
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

public class SnakeAndLaddertestv2 {
	Response response = null;
	String jsonString = null;
	String id;
	String id2;

	/**
	 * Setup basic path
	 */
	@BeforeClass
	public static void setupURL() {
		RestAssured.baseURI = "http://10.0.1.86/snl/";
		RestAssured.basePath = "/rest/v2/";
	}
	/**
	 * test response for new board
	 * @throws ParseException 
	 */
	@Test(priority = 1)
	public void test_new_board() throws ParseException {
		response =  given().auth().preemptive().basic("su", "root_pass").when().get("/board.json");
		Assert.assertEquals(response.statusCode(), 200);
		response = given().auth().preemptive().basic("su", "root_pass").when().get("/board/new.json");
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

		response =  given().auth().preemptive().basic("su", "root_pass").when().get("/board.json");
		Assert.assertEquals(response.statusCode(), 200);
		response =  given().auth().preemptive().basic("su", "root_pass").when().get("/board.json").then().contentType(ContentType.JSON).extract().response();
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
			
	response =	 given().auth().preemptive().basic("su", "root_pass").when().contentType("application/json").body(jsonObject).when().post("/player.json");
	Assert.assertEquals(response.statusCode(), 200);
	 id2=(response.getBody().jsonPath().getJsonObject("response.player.id")).toString();
	}

	/**
	 * check response for board with specific id
	 */
	@Test(priority = 6)
	public void test_board_with_id() {
		response = given().auth().preemptive().basic("su", "root_pass").when().get("/board/"+id+".json");
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
		response=given().auth().preemptive().basic("su", "root_pass").when().get("/player/"+id2+".json");
		Assert.assertEquals(response.statusCode(), 200);
		JSONParser parser = new JSONParser();	
		Object obj = parser.parse(new FileReader("C:\\Users\\sumitmishra.QAIT\\workspace\\assignment4\\src\\resource\\db.json"));
		JSONObject jsonObject = (JSONObject) obj;
		response= given().auth().preemptive().basic("su", "root_pass").contentType("application/json").body(jsonObject).when().put("/player/"+id2+".json");
		Assert.assertEquals(response.statusCode(), 200);
		 response=given().delete("/player/"+id2+".json");

	}
	/**
	 * Moving player with id
	 */
	@Test(priority=4)
	public void test_move_player(){
		response= given().auth().preemptive().basic("su", "root_pass").when().get("/move/"+id+".json?player_id="+id2);
		Assert.assertEquals(response.statusCode(), 200);
		
	}
}
	