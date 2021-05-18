package edu.uclm.esi.serverdevopsmetrics.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.serverdevopsmetrics.models.BranchOp;

@RestController
/**
 * @author FcoCrespo
 */
//src CORS: https://www.arquitecturajava.com/spring-rest-cors-y-su-configuracion/
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
		RequestMethod.DELETE }, allowedHeaders = "*")

public class GithubController {
	
	private static final Log LOG = LogFactory.getLog(GithubController.class);

	@Autowired
	BranchOp branchOperations;

	@GetMapping(value = "/branchesorder")
	public String getFirstCommitByBranch(@RequestParam("reponame") final String reponame, @RequestParam("owner") final String owner)
			throws IOException, GitAPIException, InterruptedException {

		JSONArray array = new JSONArray();
		JSONObject json;
		List<String> branches = branchOperations.getBranches(reponame, owner);
		List<String> commits = branchOperations.getFirstCommit(reponame, branches);

		for (int i = 0; i < branches.size(); i++) {
			json = new JSONObject();
			json.put("branchname", branches.get(i));
			json.put("commit", commits.get(i));
			array.put(json);
		}
		return array.toString();

	}
	
	@GetMapping(value = "/getTestReport")
	public String getFirstCommitByBranch(@RequestParam("filepath") final String filename)
			throws IOException{
		
		String [] ruta = filename.split(".json");
		String file = "C:\\Users\\Crespo\\.jenkins\\workspace\\devopsmetrics\\target\\reports\\"+ruta[0]+"\\"+filename;
		
		LOG.info(file);
		
		Path fileName = Path.of(file);
		return Files.readString(fileName);

	}

}