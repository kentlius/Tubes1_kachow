package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import javax.security.auth.login.AccountLockedException;

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
    // private final static Command DECELERATE = new DecelerateCommand();
    // private final static Command DO_NOTHING = new DoNothingCommand();

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;
    }

    public Command run() {
        List<Object> inFront = getBlocks(myCar.position.lane, myCar.position.block);

        // Situasi Fix -> jika damage 3 ke atas dan tidak punya boost (damage tidak
        // perlu sampe 0)
        if (myCar.damage > 2 && !hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            return FIX;
        }

        // Situasi jika punya boost -> Fix jika damage belum 0, Boost jika damage = 0
        if (hasPowerUp(PowerUps.BOOST, myCar.powerups) && myCar.speed < 15) {
            if (myCar.damage != 0) {
                return FIX;
            } else {
                return BOOST;
            }
        }

        // fail safe
        if (myCar.speed == 0) {
            return ACCELERATE;
        }

        // HINDAR
        if ((inFront.contains(Terrain.MUD) || inFront.contains(Terrain.OIL_SPILL) || inFront.contains(Terrain.WALL)
                || inFront.contains(Terrain.CYBER_TRUCK))) {
            // Situasi di lane 1 dan ada Obstacle
            if (myCar.position.lane == 1) {
                if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                    return LIZARD;
                } else {
                    return TURN_RIGHT;
                }
            }
            // Situasi di lane 4 dan ada Obstacle
            else if (myCar.position.lane == 4) {
                if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                    return LIZARD;
                } else {
                    return TURN_LEFT;
                }
            }
            // Situasi di lane 2 dan ada Obstacle
            else if (myCar.position.lane == 2 || myCar.position.lane == 3) {
                List<Object> inRight = getBlocks(myCar.position.lane + 1, myCar.position.block - 1);
                List<Object> inLeft = getBlocks(myCar.position.lane - 1, myCar.position.block - 1);
                if (myCar.position.lane == 2) {
                    if ((inLeft.contains(Terrain.MUD) || inLeft.contains(Terrain.OIL_SPILL)
                            || inLeft.contains(Terrain.WALL)
                            || inLeft.contains(Terrain.CYBER_TRUCK)) && (inRight.contains(Terrain.MUD)
                                    || inRight.contains(Terrain.OIL_SPILL)
                                    || inRight.contains(Terrain.WALL) || inRight.contains(Terrain.CYBER_TRUCK))) {
                        if (inLeft.size() > inRight.size()) {
                            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                                return LIZARD;
                            } else {
                                return TURN_RIGHT;
                            }
                        } else if (inLeft.size() < inRight.size()) {
                            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                                return LIZARD;
                            } else {
                                return TURN_LEFT;
                            }
                        } else {
                            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                                return LIZARD;
                            } else {
                                return ACCELERATE;
                            }
                        }
                    } else if (inLeft.contains(Terrain.MUD) || inLeft.contains(Terrain.OIL_SPILL)
                            || inLeft.contains(Terrain.WALL)
                            || inLeft.contains(Terrain.CYBER_TRUCK)) {
                        if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                            return LIZARD;
                        } else {
                            return TURN_RIGHT;
                        }
                    } else if (inRight.contains(Terrain.MUD)
                            || inRight.contains(Terrain.OIL_SPILL)
                            || inRight.contains(Terrain.WALL) || inRight.contains(Terrain.CYBER_TRUCK)) {
                        if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                            return LIZARD;
                        } else {
                            return TURN_LEFT;
                        }
                    } else {
                        if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                            return LIZARD;
                        } else {
                            return TURN_LEFT;
                        }
                    }
                }
                // Situasi di lane 3 dan ada Obstacle
                if (myCar.position.lane == 3) {
                    if ((inLeft.contains(Terrain.MUD) || inLeft.contains(Terrain.OIL_SPILL)
                            || inLeft.contains(Terrain.WALL)
                            || inLeft.contains(Terrain.CYBER_TRUCK)) && (inRight.contains(Terrain.MUD)
                                    || inRight.contains(Terrain.OIL_SPILL)
                                    || inRight.contains(Terrain.WALL) || inRight.contains(Terrain.CYBER_TRUCK))) {
                        if (inLeft.size() > inRight.size()) {
                            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                                return LIZARD;
                            } else {
                                return TURN_RIGHT;
                            }
                        } else if (inLeft.size() < inRight.size()) {
                            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                                return LIZARD;
                            } else {
                                return TURN_LEFT;
                            }
                        } else {
                            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                                return LIZARD;
                            } else {
                                return ACCELERATE;
                            }
                        }
                    } else if (inLeft.contains(Terrain.MUD) || inLeft.contains(Terrain.OIL_SPILL)
                            || inLeft.contains(Terrain.WALL)
                            || inLeft.contains(Terrain.CYBER_TRUCK)) {
                        if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                            return LIZARD;
                        } else {
                            return TURN_RIGHT;
                        }
                    } else if (inRight.contains(Terrain.MUD)
                            || inRight.contains(Terrain.OIL_SPILL)
                            || inRight.contains(Terrain.WALL) || inRight.contains(Terrain.CYBER_TRUCK)) {
                        if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                            return LIZARD;
                        } else {
                            return TURN_LEFT;
                        }
                    } else {
                        if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                            return LIZARD;
                        } else {
                            return TURN_LEFT;
                        }
                    }
                }
            } else {
                if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                    return LIZARD;
                } else {
                    return ACCELERATE;
                }
            }
        }

        // Situasi jika punya oil -> cek jika menang, jika menang maka pake oil
        if (hasPowerUp(PowerUps.OIL, myCar.powerups) && myCar.speed < 15) {
            if (myCar.position.block > opponent.position.block) {
                return OIL;
            }
        }
   
        // Situasi jika punya EMP -> cek jika menang, jika kalah tembak emp kalo lanenya dekat dengan lane musuh
        if (hasPowerUp(PowerUps.EMP, myCar.powerups) && myCar.speed < 15) {
            if (myCar.position.block < opponent.position.block) {
                if (myCar.position.lane == opponent.position.lane || myCar.position.lane == opponent.position.lane + 1 || myCar.position.lane == opponent.position.lane - 1) {
                    return EMP;
                }
            }
        }
   
        // Situasi jika punya TWEET -> pake di depan musuh
        if (hasPowerUp(PowerUps.TWEET, myCar.powerups) && myCar.speed < 15 /*&& opponent.speed > 5*/) {
            return TWEET(opponent.position.lane, opponent.position.block + opponent.speed + 1);
        }

        // Greedy PowerUP
        if ((!inFront.contains(Terrain.MUD) || !inFront.contains(Terrain.OIL_SPILL) || !inFront.contains(Terrain.WALL) || !inFront.contains(Terrain.CYBER_TRUCK))) {
            if (myCar.position.lane == 1) {
                List<Object> inRight = getBlocks(myCar.position.lane + 1, myCar.position.block - 1);
                if (inRight.contains(Terrain.BOOST)) {
                    return TURN_RIGHT;
                }
                else {
                    return ACCELERATE;
                }
            }
            else if (myCar.position.lane == 4) {
                List<Object> inLeft = getBlocks(myCar.position.lane - 1, myCar.position.block - 1);
                if (inLeft.contains(Terrain.BOOST)) {
                    return TURN_LEFT;
                }
                else {
                    return ACCELERATE;
                }

            }
            else if (myCar.position.lane == 2 || myCar.position.lane == 3) {
                List<Object> inRight = getBlocks(myCar.position.lane + 1, myCar.position.block - 1);
                List<Object> inLeft = getBlocks(myCar.position.lane - 1, myCar.position.block - 1);
                if (inRight.contains(Terrain.BOOST)) {
                    return TURN_RIGHT;
                } 
                else if (inLeft.contains(Terrain.BOOST)) {
                    return TURN_LEFT;
                }
                else {
                    return ACCELERATE;
                }
            }
            return ACCELERATE;
        }

        return ACCELERATE;
    }

    // Mengecek apakah kachow memiliki power up
    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp : available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    // Mengecek block disekitar kachow
    private List<Object> getBlocks(int lane, int block) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        if (myCar.speed == 15) {
            for (int i = max(block - startBlock, 0); i <= block - startBlock + 15; i++) {
                if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                    break;
                }
    
                blocks.add(laneList[i].terrain);
    
            }
        } else {
            for (int i = max(block - startBlock, 0); i <= block - startBlock + Bot.maxSpeed; i++) {
                if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                    break;
                }
    
                blocks.add(laneList[i].terrain);
    
            }
        }
        
        return blocks;
    }

    // Jalankan TWEET pada koordinat tertentu
    private Command TWEET(int lane, int block) {
        return new TweetCommand(lane, block);
    }
}
