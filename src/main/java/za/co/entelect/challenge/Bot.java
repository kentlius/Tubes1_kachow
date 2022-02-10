package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

public class Bot {

    private static final int maxSpeed = 9;
    private List<Integer> directionList = new ArrayList<>();

    private Random random;
    private GameState gameState;
    private Car opponent;
    private Car myCar;
    private final static Command FIX = new FixCommand();
    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);
    private final static Command DECELERATE = new DecelerateCommand();
    private final static Command DO_NOTHING = new DoNothingCommand();
    // private final static Command TWEET = new TweetCommand(4, 76); //lane 4, block 47

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;
    }

    public Command run() {
        List<Object> inFront = getBlocks(myCar.position.lane, myCar.position.block);
        if(myCar.position.lane == 1){
            if(inFront.contains(Terrain.MUD) || inFront.contains(Terrain.WALL)){
                return TURN_RIGHT;
            }
        }
        else if(myCar.position.lane == 4){
            if(inFront.contains(Terrain.MUD) || inFront.contains(Terrain.WALL)){
                return TURN_LEFT;
            }
        }
        else {
            List<Object> inRight = getBlocksSide(myCar.position.lane + 1, myCar.position.block - 1);
            List<Object> inLeft = getBlocksSide(myCar.position.lane - 1, myCar.position.block - 1);
            if(inRight.contains(Terrain.MUD)){
                return ACCELERATE;
            }
            if((inFront.contains(Terrain.MUD) || inFront.contains(Terrain.WALL)) && myCar.position.lane == 2){
                return TURN_RIGHT;
            }
            else if(inFront.contains(Terrain.MUD) &&  inLeft.contains(Terrain.MUD)) {
                return TURN_RIGHT;
            }
            else if((inFront.contains(Terrain.MUD) || inFront.contains(Terrain.WALL)) && myCar.position.lane == 3){
                return TURN_LEFT;
            }
        }
        if (myCar.damage > 1) {
            return FIX;
        }
        if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            return BOOST;
        }
        if (hasPowerUp(PowerUps.OIL, myCar.powerups)) {
            return OIL;
        }
        return ACCELERATE;
    }

    //Mengecek apakah kachow memiliki power up
    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp: available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    //Mengecek sisi depan dari kachow
    private List<Object> getBlocks(int lane, int block) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + Bot.maxSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

        }
        return blocks;
    }

    //Mengecek sisi samping dari kachow
    private List<Object> getBlocksSide(int lane, int block) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + Bot.maxSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }
            blocks.add(laneList[i].terrain);
        }
        return blocks;
    }

    //Kembaliin true jika kachow lagi di depan musuh
    private Boolean isWinning() {
        if(myCar.position.block > opponent.position.block) {
            return true;
        }
        return false;
    }

}
