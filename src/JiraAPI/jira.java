package JiraAPI;

import static io.restassured.RestAssured.given;

import java.io.File;

import org.testng.Assert;

import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;

public class jira {

	
		// TODO Auto-generated method stub

		public static void main(String[] args) {
			// TODO Auto-generated method stub
			String idd;
			RestAssured.baseURI="http://localhost:8080";
			//authentication steps
			SessionFilter session=new SessionFilter();
			String givenAuthenticationResponse=
			given().log().all().relaxedHTTPSValidation().header("Content-Type","application/json").body("{\r\n"
					+ "    \"username\":\"Mahmoud\",\r\n"
					+ "    \"password\":\"Mahmoud.hatem1\"\r\n"
					+ "}").filter(session).when().post("/rest/auth/1/session").then().log().all().assertThat().statusCode(200).extract().response().asString();
			JsonPath js=new JsonPath(givenAuthenticationResponse);
			String value=js.getString("session.value");
			System.out.println(value);
			//create issue
			
			String createdIssueResponse=
			given().log().all().header("Content-Type","application/json").body("{\r\n"
					+ "    \"fields\": {\r\n"
					+ "       \"project\":\r\n"
					+ "       {\r\n"
					+ "          \"key\": \"AUT\"\r\n"
					+ "       },\r\n"
					+ "       \"summary\": \"debit cart defect.\",\r\n"
					+ "       \"description\": \"Creating of an issue using ids for projects and issue types using the REST API\",\r\n"
					+ "       \"issuetype\": {\r\n"
					+ "          \"name\": \"Bug\"\r\n"
					+ "       }\r\n"
					+ "   }\r\n"
					+ "}").filter(session).when().post("http://localhost:8080/rest/api/2/issue").then().log().all().assertThat().statusCode(201)
			.extract().response().asString();
			
			JsonPath js1=new JsonPath(createdIssueResponse);
			idd=js1.getString("id");
			System.out.println(idd);
			
			
			//Add comment
			String expected="hi hi";
			 //System.out.println(10311);
			String commentResponseId=
			given().log().all().pathParam("id", idd).header("Content-Type","application/json").body("{\r\n"
					+ "    \"body\":\""+expected+"\",\r\n"
					+ "    \"visibility\":{\r\n"
					+ "        \"type\":\"role\",\r\n"
					+ "        \"value\":\"Administrators\"\r\n"
					+ "    }\r\n"
					+ "}").filter(session).when().post("/rest/api/2/issue/{id}/comment").then().log().all().assertThat().statusCode(201).extract().response().asString();

			JsonPath commentResponse=new JsonPath(commentResponseId);
			String commentId=js.getString("id");
			//Add attachment
			
			given().log().all().pathParam("id", idd ).header("X-Atlassian-Token","no-check").filter(session).multiPart("file",new File("jira.txt")).header("Content-Type","multipart/form-data").when().post("/rest/api/2/issue/{id}/attachments");
		
		//get request
			
			String getResponse=
			given().filter(session).queryParam("fields", "comment").pathParam("id",idd ).when().get("/rest/api/2/issue/{id}").then().log().all().assertThat().statusCode(200).extract().response().asString();
			System.out.println(getResponse);
			
		//THE LAST STEP WHIC IS VALIDATION
			JsonPath js2=new JsonPath(getResponse);
			int size=js.getInt("fields.comment.comments.size()");
			for(int i=0;i<size;i++) {
				String iddd=js2.get("fields.comment.comments.["+i+"].id");
				if(commentId.equalsIgnoreCase(iddd)) {
					String bodyy=js2.getString("fields.comment.comments.["+i+"].body");
					System.out.println(bodyy);
					Assert.assertEquals(bodyy,expected );
					break;
				}
			}
			
			
			
			
		
		}

	}


