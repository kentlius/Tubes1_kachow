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
        // Situasi di lane 1 dan ada Obstacle
        if(myCar.position.lane == 1){
            if(inFront.contains(Terrain.MUD) || inFront.contains(Terrain.WALL) || inFront.contains(Terrain.OIL_SPILL)){
                if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)){
                    return LIZARD;
                } else {
                    return TURN_RIGHT;
                }
            }
        }
        // Situasi di lane 4 dan ada Obstacle
        else if(myCar.position.lane == 4){
            if(inFront.contains(Terrain.MUD) || inFront.contains(Terrain.WALL) || inFront.contains(Terrain.OIL_SPILL)){
                if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)){
                    return LIZARD;
                } else {
                    return TURN_LEFT;
                }
            }
        }
        // Situasi bukan di lane 1 atau 4 dan ada obstacle -> pilih belok dengan melihat lane mana yang tidak ada obstacle
        else {
            List<Object> inRight = getBlocksSide(myCar.position.lane + 1, myCar.position.block - 1);
            List<Object> inLeft = getBlocksSide(myCar.position.lane - 1, myCar.position.block - 1);
            if(inRight.contains(Terrain.MUD) || inRight.contains(Terrain.OIL_SPILL)){
                return ACCELERATE;
            }
            if((inFront.contains(Terrain.MUD) || inFront.contains(Terrain.WALL) || inFront.contains(Terrain.OIL_SPILL)) && myCar.position.lane == 2){
                if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)){
                    return LIZARD;
                } else {
                    return TURN_RIGHT;
                }
            }
            else if((inFront.contains(Terrain.MUD) || inFront.contains(Terrain.OIL_SPILL)) &&  (inLeft.contains(Terrain.MUD) || inLeft.contains(Terrain.OIL_SPILL))) {
                if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)){
                    return LIZARD;
                } else {
                    return TURN_RIGHT;
                }
            }
            else if((inFront.contains(Terrain.MUD) || inFront.contains(Terrain.WALL) || inFront.contains(Terrain.OIL_SPILL)) && myCar.position.lane == 3){
                if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)){
                    return LIZARD;
                } else {
                    return TURN_LEFT;
                }
            }
        }
        // Situasi Fix -> jika damage 3 ke atas dan tidak punya boost (damage tidak perlu sampe 0)
        if (myCar.damage > 2 && !hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            return FIX;
        }
        // Situasi jika punya boost -> Fix jika damage belum 0, Boost jika damage = 0
        if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            if (myCar.damage!=0){
                return FIX;
            } else {
                return BOOST;
            }
        }
        // Situasi jika punya oil -> langsung pake
        if (hasPowerUp(PowerUps.OIL, myCar.powerups)) {
            return OIL;
        }
        // Situasi jika punya EMP -> cek jika menang, jika kalah tembak emp kalo lanenya dekat dengan lane musuh
        if (hasPowerUp(PowerUps.EMP, myCar.powerups)){
            if(isWinning()){
                return ACCELERATE;
            } else {
                if (myCar.position.lane==opponent.position.lane||myCar.position.lane==opponent.position.lane+1||myCar.position.lane==opponent.position.lane-1){
                    return EMP;
                } else {
                    return ACCELERATE;
                }
            }
        }
        // Situasi jika punya TWEET -> pake di depan musuh
        if (hasPowerUp(PowerUps.TWEET, myCar.powerUp)){
            return TweetCommand(opponent.position.lane,opponent.position.block+1);
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
