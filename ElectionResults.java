import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class ElectionResults {

	// the main method works as follows:
	// - provided code (leave this code as is):
	//   - prompts user for file name containing ballot data
	//   - reads data into array (one array item per line in file)
	//   - runs any testing code that you have written
	// - code you need to write:
	//   - execute the Ranked Choice Voting process as outlined
	//     in the project description document by calling the other
	//     methods that you will implement in this project
	public static void main(String[] args) {

		// Establish console Scanner for console input
		Scanner console = new Scanner(System.in);

		// Determine the file name containing the ballot data
		System.out.print("Ballots file: ");
		String fileName = console.nextLine();

		// Read the file contents into an array.  Each array
		// entry corresponds to a line in the file.
		String[] fileContents = getFileContents(fileName);



		ArrayList<Ballot> ballotChoices = convert(fileContents);


		HashMap<String, Integer> voteCount = tallies(ballotChoices);



		int total = ballotChoices.size();

		boolean result = true;
		while (result) {
			Result winnerOrLoser = analyze(voteCount, total);
			if (winnerOrLoser.isWinner()) {
				printCounts(voteCount);
				System.out.println();
				System.out.println(winnerOrLoser.getName() + " is the winner.");
				System.out.println();
				System.out.println("Vote Percentages");
				printPercentages(voteCount, total);
				result = false;
			}
			else {
				printCounts(voteCount);
				String loser = winnerOrLoser.getName();
				System.out.println();
				System.out.println(loser + " is eliminated.");
				System.out.println();
				remove(loser, ballotChoices);
				voteCount = tallies(ballotChoices);
				total = ballotChoices.size();

			}
		}









		// Run any testing code that has been written
		//test();


		// Your code below here: execute the RCV process,
		// ensuring to make use of the remaining methods

	}


	// Ballot documentation: https://docs.google.com/document/d/1E4uDFs_L6-XYHlAspWg91jaawopdnXbOeYgje252ODA/edit?usp=sharing
	// Result documentation: https://docs.google.com/document/d/1C0acJ1W5kTwjnUpqJ8nJDN75qU891CmOjxQyKVR2_lY/edit?usp=sharing


	// this method is for you to write testing code
	// write anything you like here, or nothing at all
	// nothing you write here will be graded
	//public static void test() {




	// method name: convert()
	// - input: rawData, an array of String data from the input file
	//   - each String in the array represents one ballot in the election
	//   - a ballot with 2+ candidates will separate each name by a comma
	//   - a ballot with 1 candidate with have the name only, no comma
	//   - candidates are listed in preference order (1st choice,2nd choice,etc.)
	// - action: convert each string in the array to a Ballot object and
	//           create a list of all of the Ballot objects
	// - output: the list of Ballot objects
	public static ArrayList<Ballot> convert(String[] rawData) {
		ArrayList<Ballot> ballotObject = new ArrayList<Ballot>();

		for (String ballotChoice : rawData) {
			Ballot newBallot = new Ballot();
			if (ballotChoice.contains(",")) {
				String [] moreThanOne = ballotChoice.split(",");
				for (String candidate : moreThanOne) {
					newBallot.addCandidate(candidate);
				}
				ballotObject.add(newBallot);
			}
			else {
				newBallot.addCandidate(ballotChoice);	
				ballotObject.add(newBallot);
			}
		}



		return ballotObject; 
	}


	// methodName: tallies()
	// - input: ballots, a list of Ballot objects representing all election ballots
	// - action: determine the number of votes earned by each candidate
	// - output: a HashMap with candidates as keys and vote counts as values
	public static HashMap<String, Integer> tallies(ArrayList<Ballot> ballots) {
		HashMap<String, Integer> tallyMap = new HashMap<String, Integer>();
		ArrayList<String> candidateCountList = new ArrayList<String>();
		for (Ballot candidateBallot : ballots) {
			String firstCandidate = candidateBallot.getCurrentChoice();
			candidateCountList.add(firstCandidate);
		}
		for (String candidate : candidateCountList) {
			if (tallyMap.containsKey(candidate)) {
				tallyMap.put(candidate, tallyMap.get(candidate)+1);

			}
			else {
				tallyMap.put(candidate, 1);
			}
		}


		return tallyMap; 
	}


	// method name: analyze()
	// - inputs:
	//   - tally, a HashMap from candidate name (keys) to vote tally (values)
	//   - total, an int indicating the total number of ballots
	// - action: determine whether there is a winner or loser, and who it is
	// - output: a Result object based on the action above
	public static Result analyze(HashMap<String, Integer> tally, int total) {
		ArrayList<Integer> lessThanMajority = new ArrayList<Integer>();
		double majorityVotes = Math.round((double) total / 2);
		Result winner = new Result();
		boolean win= false;
		for (String key : tally.keySet()) {
			int numberOfVotes = tally.get(key);
			if (numberOfVotes > majorityVotes) {
				winner.setName(key);
				winner.setWin(true);
				win =true;
			}
			else {
				lessThanMajority.add(tally.get(key));
			}
		}
		Result loser = new Result();
		if (!lessThanMajority.isEmpty()) {
			int min = lessThanMajority.get(0);
			for (int num : lessThanMajority) {
				if (num < min) {
					min = num;
				}

			}
			for (String key : tally.keySet()) {
				if (min == tally.get(key)) {
					loser.setName(key);
					loser.setWin(false);
				}
			}
		}
		if (win == true) {
			return winner;
		}
		return loser;

	}


	// method name: printCounts()
	// - input: tally, a HashMap from candidate name (keys) to vote tally (values)
	// - action: prints the following information, one line per map key
	//   - candidate name
	//   - a blank space
	//   - number of votes earned
	public static void printCounts(HashMap<String, Integer> tally) {
		for (String key: tally.keySet()) {
			System.out.println(key + " " + tally.get(key));
		}


	}


	// method name: remove()
	// - inputs:
	//   - name, a String indicating a candidate name to remove
	//   - ballots, a list of Ballot objects representing all election ballots
	// - action:
	//   - remove the candidate from each ballot where the candidate appears
	//   - remove all exhausted ballots from the list
	public static void remove(String name, ArrayList<Ballot> ballots) {
		for (Ballot choices : ballots) {
			choices.removeCandidate(name);
		}
		//System.out.println(ballots);

		for (int i = ballots.size() - 1; i >= 0; i--) {
			if (ballots.get(i).isExhausted()) {
				ballots.remove(i);
			}
		}


	}


	// method name: printPercentages()
	// - inputs:
	//   - tally, a HashMap from candidate name (keys) to vote tally (values)
	//   - total, an int indicating the total number of ballots
	// - action: prints the following information, one line per map key
	//   - percentage of vote earned, rounded to 1 decimal place
	//   - a blank space
	//   - candidate name
	public static void printPercentages(HashMap<String, Integer> tally, int total) {
		for (String candidate : tally.keySet()) {
			double numOfVotes = (double) tally.get(candidate);
			double percent = (numOfVotes / total) * 100;
			System.out.printf("%.1f" + "%%" + " " + candidate, percent);
			System.out.println();
		}


	}


	// methods below here are already complete

	public static String[] getFileContents(String fileName) {

		// first pass: determine number of lines in the file
		Scanner file = getFileScanner(fileName);
		int numLines = 0;
		while (file.hasNextLine()) {
			file.nextLine();
			numLines++;
		}

		// create array to hold the number of lines counted
		String[] contents = new String[numLines];

		// second pass: read each line into array
		file = getFileScanner(fileName);
		for (int i = 0; i < numLines; i++) {
			contents[i] = file.nextLine();
		}

		return contents;
	}


	public static Scanner getFileScanner(String fileName) {
		try {
			FileInputStream textFileStream = new FileInputStream(fileName);
			Scanner inputFile = new Scanner(textFileStream);
			return inputFile;
		}
		catch (IOException ex) {
			System.out.println("Warning: could not open " + fileName);
			return null;
		}
	}
}
