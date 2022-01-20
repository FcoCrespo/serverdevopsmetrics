package edu.uclm.esi.serverdevopsmetrics.controllers;

import edu.uclm.esi.serverdevopsmetrics.models.*;

@RestController
@CrossOrigin(origins="*", methods={RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}, allowedHeaders="*")
public class GithubController {

	private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(GithubController.class);
	@Autowired
	BranchOp branchOperations;

	/**
	 * 
	 * @param reponame
	 * @param owner
	 * @param tokenGithub
	 */
	@GetMapping("/branchesorder")
	public String getFirstCommitByBranch(@RequestParam("reponame")
	final String reponame, @RequestParam("owner")
	final String owner, @RequestParam("tokenGithub")
	final String tokenGithub) throws java.io.IOException, org.eclipse.jgit.api.errors.GitAPIException, InterruptedException {
		// TODO - implement GithubController.getFirstCommitByBranch
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param filename
	 */
	@GetMapping("/getTestReport")
	public String getFirstCommitByBranch(@RequestParam("filepath")
	final String filename) throws java.io.IOException {
		// TODO - implement GithubController.getFirstCommitByBranch
		throw new UnsupportedOperationException();
	}

}