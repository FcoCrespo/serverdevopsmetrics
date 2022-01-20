package edu.uclm.esi.serverdevopsmetrics.models;

@Service
@Scope("singleton")
public class BranchOp {

	private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(BranchOp.class);
	private String dir = "C:/Github/";
	private String resourcessalida = "C:/resources/salida-";
	private String branchStr = "-branch-";
	private String slash = "/";
	private String guion = "-";

	/**
	 * 
	 * @param reponame
	 * @param owner
	 * @param tokenGithub
	 */
	public java.util.List<String> getBranches(String reponame, String owner, String tokenGithub) throws java.io.IOException, InterruptedException {
		// TODO - implement BranchOp.getBranches
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param ownerEndParenthesis
	 * @param reponEndParenthesis
	 */
	private java.util.List<String> getBranches(String ownerEndParenthesis, String reponEndParenthesis) {
		// TODO - implement BranchOp.getBranches
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param reponame
	 * @param owner
	 * @param branches
	 */
	public java.util.List<String> getFirstCommit(String reponame, String owner, java.util.List<String> branches) throws java.io.IOException, org.eclipse.jgit.api.errors.GitAPIException, InterruptedException {
		// TODO - implement BranchOp.getFirstCommit
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param branches
	 * @param reponame
	 * @param owner
	 * @param option
	 */
	private java.util.List<String> execute(java.util.List<String> branches, String reponame, String owner, String option) throws java.io.IOException, InterruptedException {
		// TODO - implement BranchOp.execute
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param reponame
	 * @param branch
	 * @param owner
	 */
	private String obtenerResultado(String reponame, String branch, String owner) {
		// TODO - implement BranchOp.obtenerResultado
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param reponame
	 * @param owner
	 * @param branch
	 * @param lastLine
	 */
	private void updatefile(String reponame, String owner, String branch, String lastLine) {
		// TODO - implement BranchOp.updatefile
		throw new UnsupportedOperationException();
	}

}