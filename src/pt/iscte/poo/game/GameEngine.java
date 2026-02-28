package pt.iscte.poo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import objects.*;
import objects.Characters.BigFish;
import objects.Characters.Krab;
import objects.Characters.SmallFish;
import objects.Movable.Bomb;
import objects.Movable.Stone;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import java.util.List;

import pt.iscte.poo.utils.Point2D;

public class GameEngine implements Observer {

	private Map<String, Room> rooms;
	private Room currentRoom;
	private int lastTickProcessed = 0;
	private String currentRoomName;
	private int timeElapsed = 0;
	private int allMoves = 0;
	private int totalTime = 0;
	private long lastTimeMillis;
	private int currentLevelNumber;
	private File[] roomFiles;

    private DeathManager deathManager;

	// Transition variables
	private boolean isTransitioning;
	private int transitionTicks;
	private final int TRANSITION_DELAY = 3;

    private boolean deathTransition;
    private int deathDelayTicks;
    private final int DEATH_DELAY = 2;
    
    private boolean isClear = false;
    
	private ImageGUI gui;
	
    class Stat {
        int time;
        int moves;
        Stat(int t, int m) {
            time = t;
            moves = m;
        }
    }
    
	public GameEngine() {
		gui = ImageGUI.getInstance();
		rooms = new HashMap<String, Room>();
		loadGame();
		currentLevelNumber = 0;
		isTransitioning = false;
		transitionTicks = 0;
        deathTransition = false;
        deathDelayTicks = 0;
		loadLevel(currentLevelNumber);
		loadMusic();
        deathManager = new DeathManager(currentRoom);

		updateStatusBar();
	}

	public void playSound(String path) {
	    try {
	        AudioInputStream sndIS = AudioSystem.getAudioInputStream(new File(path));
	        Clip sndClip = AudioSystem.getClip();
	        sndClip.open(sndIS);
	        sndClip.start();
	    }
	    catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
	    	//ignore
	    }
	}
	
	private void loadGame() {
		roomFiles = new File("./rooms").listFiles();
		for (File f : roomFiles) {
			rooms.put(f.getName(), Room.readRoom(f, this));
		}
	}
	
	private void loadMusic() {
	    File audioFile = new File("sounds/musTheme.wav");
		try (AudioInputStream musIS = AudioSystem.getAudioInputStream(audioFile)) {
		    Clip musClip = AudioSystem.getClip();
		    musClip.open(musIS);
		    musClip.loop(Clip.LOOP_CONTINUOUSLY);
		    musClip.start();
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			//ignore
		}
	}
	
	private void reset() {
		rooms.clear();
		loadGame();
		currentLevelNumber = 0;
		isTransitioning = false;
		transitionTicks = 0;
        deathTransition = false;
        deathDelayTicks = 0;
        lastTickProcessed = 0;
    	timeElapsed = 0;
    	allMoves = 0;
    	totalTime = 0;
    	lastTimeMillis = 0;
		loadLevel(currentLevelNumber);
        deathManager = new DeathManager(currentRoom);
		updateStatusBar();
	}
	
	private void loadLevel(int levelNumber) {

		if (roomFiles == null || levelNumber < 0 || levelNumber >= roomFiles.length) {
			return;
		}

		currentLevelNumber = levelNumber;
		File roomFile = roomFiles[levelNumber];
		currentRoomName = roomFile.getName();

		if (currentRoom != null) {
			ImageGUI.getInstance().clearImages();
		}

		SmallFish.resetInstance();
		BigFish.resetInstance();

		// Load the room from file
		currentRoom = Room.readRoom(roomFile, this);
		rooms.put(currentRoomName, currentRoom);

		gui.setStatusMessage("Loading level: " + currentRoomName);

		SmallFish.getInstance().resetMoveCount();
		BigFish.getInstance().resetMoveCount();

		// Set rooms for fish
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
		// Reset time elapsed
		lastTimeMillis = System.currentTimeMillis();

        if(deathManager != null) {
            deathManager.setRoom(currentRoom);
        }
        totalTime += timeElapsed;
        timeElapsed = 0;
		updateGUI();
		updateStatusBar();

	}

	/**
	 * New method to restart the current level, resetting all necessary variables and reloading the room from file. Called when player presses R or dies.
	 *
	 */
	public void restartLevel() {

		ImageGUI.getInstance().clearImages();

        currentRoom = null;

        //Remove old room from map to force fresh load
        if(currentRoomName != null && rooms.containsKey(currentRoomName)) {
            rooms.remove(currentRoomName);
        }

		SmallFish.resetInstance();
		BigFish.resetInstance();

		timeElapsed = 0;
		lastTimeMillis = System.currentTimeMillis();

        deathTransition = false;
        deathDelayTicks = 0;

		// Reload the room from file
		loadLevel(currentLevelNumber);


		updateGUI();
		updateStatusBar();

		gui.setStatusMessage("Level restarted!");

	}

	@Override
	public void update(Observed source) {

		if (!isTransitioning && !deathTransition) {
			// Update elapsed seconds based on real time so it counts even when idle
			long now = System.currentTimeMillis();
			long diff = now - lastTimeMillis;
			if (diff >= 1000) {
				timeElapsed += (int) (diff / 1000);
				lastTimeMillis = now - (diff % 1000);
				updateStatusBar();
			}
		}

		if (!isTransitioning && !deathTransition && ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();

			// When R is pressed, restart level
			if (k == java.awt.event.KeyEvent.VK_R) {
				if (!isClear)
					restartLevel();
				else
					reset();
				return;
			}
			// When ESC is pressed, close the game
			if (k == java.awt.event.KeyEvent.VK_ESCAPE) {
				System.exit(0);
			}
			// When space is pressed, switch active character
			if (k == java.awt.event.KeyEvent.VK_SPACE) {
				currentRoom.switchActiveCharacter();// Method in Room class to switch active character
			} else {
				// Move active character in the direction of the arrow key pressed
				try {
					Direction dir = Direction.directionFor(k);
					GameObject active = currentRoom.getActiveCharacter();
					if (active instanceof GameCharacter) {
						// Update fish images based on direction
						((GameCharacter) active).updateFishImages(dir);
						// Move the active character
						if (((GameCharacter) active).move(dir.asVector())) {
							playSound("sounds/sndSwim.wav");
							for (GameObject obj : currentRoom.getObjects()) {
				                if (obj instanceof Krab) {
				                	((Krab) obj).move(null);
				                }
				            }
						}
						updateStatusBar();
					}
				} catch (IllegalArgumentException e) {
					// Key pressed wasn't a direction key, ignore
				}
			}
		}
		int t = ImageGUI.getInstance().getTicks();
        if (!isTransitioning && !deathTransition && t != lastTickProcessed) {
            // Update falling objects
            List<GameObject> allObjects = currentRoom.getObjects();

            for (GameObject obj : allObjects) {
                if (obj instanceof MobileObjects) {
                    ((MobileObjects) obj).updateFall();
                }

                if(obj instanceof Bomb) {
                	if (((Bomb) obj).hasExploded())
                		playSound("sounds/sndBomb.wav");
                    ((Bomb) obj).updateExplosionTimer();
                }

                if(obj instanceof Blood){
                    ((Blood) obj).update();
                }
                
                if(obj instanceof Stone){
                    ((Stone) obj).spawnKrab();
                }
            }
            checkDeaths();

            currentRoom.processPendingChanges();

        }

        if(deathTransition){
            processDeathTransition();
        }

        
		while (lastTickProcessed < t) {
			processTick();
		}
		ImageGUI.getInstance().update();

	}

    private void checkDeaths(){

        if(deathManager == null){
            return;
        }

        SmallFish sf = SmallFish.getInstance();
        BigFish bf = BigFish.getInstance();

        if(!sf.isAlive() || !bf.isAlive()){
            startDeathTransition();
        }

       if (deathManager.checkDeathConditions(sf) || deathManager.checkDeathConditions(bf)) {
            startDeathTransition();
        }

    }

    private void startDeathTransition() {
    	playSound("sounds/sndDeath.wav");
        deathTransition = true;
        deathDelayTicks = DEATH_DELAY;

        //remove fishes from room to avoid further interaction
        SmallFish sf = SmallFish.getInstance();
        BigFish bf = BigFish.getInstance();
        GameObject blood = new Blood(currentRoom);

        if (!sf.isAlive()) {
            currentRoom.scheduleRemoveObject(sf);
            addBloodAtPosition(sf.getPosition());
        }
        if (!bf.isAlive()) {
            currentRoom.scheduleRemoveObject(bf);
            addBloodAtPosition(bf.getPosition());
        }

    }

    private void addBloodAtPosition(Point2D position) {
        GameObject blood = new Blood(currentRoom);
        blood.setPosition(position);
        currentRoom.scheduleAddObject(blood);
    }

    public void onFishDiedFromPushing() {
        deathTransition = true;
        deathDelayTicks = DEATH_DELAY;
    }

    private void processDeathTransition() {
        deathDelayTicks--;
        if (deathDelayTicks <= 0) {
            // Restart level after death transition
            deathTransition = false;

            gui.showMessage("GameOver", "A fish Died! Restarting Level..." );

            restartLevel();
        }
    }

	private void processTick() {
		lastTickProcessed++;

		if (isTransitioning) {
			processLevelTransition();
		}
	}

	public void updateGUI() {
		if (currentRoom != null) {
			ImageGUI.getInstance().clearImages();
			ImageGUI.getInstance().addImages(currentRoom.getObjects());
		}
	}

	public void updateStatusBar() {

		// If in transition, skip status update
		if (isTransitioning) {
			return;
		}
		// Get fish instances
		SmallFish sf = SmallFish.getInstance();
		BigFish bf = BigFish.getInstance();

		// Build status message
		StringBuilder status = new StringBuilder();
		status.append("Level: ").append(currentLevelNumber);

		// Show fish moves with exit indicator
		status.append(" | SF Moves: ").append(sf.getMoveCount());
		if (sf.hasExited()) {
			status.append(" [EXITED]");
		}

		status.append(" | BF Moves: ").append(bf.getMoveCount());
		if (bf.hasExited()) {
			status.append(" [EXITED]");
		}

		// Show selected fish
		GameObject active = currentRoom.getActiveCharacter();
		if (active != null) {
			status.append(" | Selected: ");
			status.append(active instanceof SmallFish ? "Small Fish" : "Big Fish");
		}

		status.append(" | Time: ").append(timeElapsed).append(" s");

		gui.setStatusMessage(status.toString());
	}

	// Method for counting time elapsed
	public int getTimeElapsed() {
		return timeElapsed;
	}

	public void onLevelCompleted() {
		allMoves += SmallFish.getInstance().getMoveCount() + BigFish.getInstance().getMoveCount();
		// Start transition to next level
		isTransitioning = true;
		transitionTicks = TRANSITION_DELAY;
	}

	private String getTableString() {
	    File file = new File("score.txt");
	    if (!file.exists()) {
	    	System.err.println("File not found");
	        return "";
	    }
	    String result = "";
	    result += "★★★ Top 10 Records ★★★\n";
	    result += String.format("%-6s %-6s %s\n", "Rank", "Time", "Moves");
	    try (Scanner scanner = new Scanner(file)) {
	        int l = 1;
	        while (scanner.hasNextLine()) {
	            String line = scanner.nextLine().trim();
	            if (line.isEmpty()) continue;
	            String[] p = line.split(",");
	            if (p.length != 2)
	            	continue;
	            int t = Integer.parseInt(p[0]);
	            int m = Integer.parseInt(p[1]);
	            result += String.format("%-11d %-11d %d\n",l,t,m);
	            l++;
	        }
		    while(l < 10)
		    {
		    	result += String.format("%-11d %-11d %d\n",l,0,0);
		    	l++;
		    }
	    }
	    catch (Exception e) {
	        return "Error reading score file.";
	    }
	    return result;
	}
	
	private void saveToFile() {
	    File file = new File("score.txt");
	    List<Stat> scores = new ArrayList<>();
	    if (file.exists()) {
	        try (Scanner scanner = new Scanner(file)) {
	            while (scanner.hasNextLine()) {
	                String line = scanner.nextLine().trim();
	                if (!line.isEmpty()) {
	                    String[] p = line.split(",");
	                    if (p.length == 2) {
	                        try {
	                            int t = Integer.parseInt(p[0]);
	                            int m = Integer.parseInt(p[1]);
	                            scores.add(new Stat(t,m));
	                        }
	                        catch (NumberFormatException e)
	                        {
	                        	System.err.println("Error parsing score entry: " + line);
	                        }
	                    }
	                }
	            }
	        }
	        catch (FileNotFoundException e)
	        {
	            System.err.println("Error reading score file.");
	        }
	    }
	    scores.add(new Stat(totalTime,allMoves));
	    scores.sort((a, b) -> Integer.compare(a.time,b.time));
	    if (scores.size() > 10) {
	        scores = scores.subList(0, 10);
	    }
	    try (PrintWriter writer = new PrintWriter(file)) {
	        for (Stat e : scores) {
	            writer.println(e.time + "," + e.moves);
	        }
	    } catch (FileNotFoundException e) {
	        System.err.println("Error writing score file.");
	    }
	}
	
	private void processLevelTransition() {
		if (!isTransitioning) {
			return;
		}
		transitionTicks--;
		if (transitionTicks <= 0) {
            //show message after transition delay
            int completedLevel = currentLevelNumber;
            int totalMoves = SmallFish.getInstance().getMoveCount() + BigFish.getInstance().getMoveCount();
			// Load next level
			isTransitioning = false;
			int nextLevel = currentLevelNumber + 1;
			if (nextLevel < roomFiles.length) {
                gui.showMessage("Level Finish!","★★★ LEVEL " + completedLevel + " COMPLETE! ★★★  " +
                        "\nMoves: " + totalMoves + " | Time: " + timeElapsed + "s" +
                        "\nLoading next level...");
				loadLevel(nextLevel);
			} else {
				// Game completed
				gameComplete();
			}
		}
	}

	// Method Created for game completion
	private void gameComplete() {
        totalTime += timeElapsed;
        saveToFile();
		String table = getTableString();
		gui.showMessage("Game Completion","★★★ GAME COMPLETE! ★★★  \nYou finished all " + roomFiles.length + " levels!\n\n"+table+"\nPress R to restart");
		isTransitioning = false;
		isClear = true;
	}

	/**
	 * Switch Room in Game Engine
	 * 
	 * @param roomName Name of the room to switch to
	 */
	public void switchRoom(String roomName) {

		if (rooms.containsKey(roomName)) {
			currentRoomName = roomName;
			currentRoom = rooms.get(roomName);

			// Reset fish room references
			SmallFish.resetInstance();
			BigFish.resetInstance();
			SmallFish.getInstance().setRoom(currentRoom);
			BigFish.getInstance().setRoom(currentRoom);
			updateGUI();
			updateStatusBar();
		} else
			System.err.println("Room " + roomName + " does not exist.");
	}

	public Room getCurrentRoom() {
		return currentRoom;
	}

	public String getCurrentRoomName() {
		return currentRoomName;
	}

	public int getCurrentLevelNumber() {
		return currentLevelNumber;
	}

}
