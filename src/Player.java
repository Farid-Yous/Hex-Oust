import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Player {
    private String name;
    private String colour;
    private int numCells;
    public HashMap<Integer, HashSet<HexCube>> groups; // Hashmap for o(1) lookup
    private int nextGroupId; // To keep track of group IDs
    public Player(String name, String colour) {
        if (colour == null || (!colour.equals("blue") && !colour.equals("red"))) {
            throw new IllegalArgumentException("Invalid color! Choose 'blue' or 'red'.");
        }
        this.name = name;
        this.colour = colour;
        this.numCells = 0;
        this.groups = new HashMap<>();
        this.nextGroupId = 1;
    }
    public boolean isInGroup(HexCube cell) {//checks to see if the cell belongs to a group
        for (HashSet<HexCube> group : groups.values()) {
            if (group.contains(cell)) {
                return true;
            }
        }
        return false;
    }

    public Integer getGroupId(HexCube cell) { //returns the group ID
        for (HashMap.Entry<Integer, HashSet<HexCube>> entry : groups.entrySet()) {
            if (entry.getValue().contains(cell)) {
                return entry.getKey();
            }
        }
        return null;
    }
    /*
    addToGroup takes a cell and a neighbour, and adds the cell to the neighbours group, if the neighbour isnt in a group, a new group is formed with the 2 cells
     */
    public void addToGroup(HexCube cell, HexCube neighbor) { //adds the cell to an existing group
        Integer neighborGroupId = getGroupId(neighbor);

        if (neighborGroupId != null) {
            // If neighbor is in a group, add cell to that group
            groups.get(neighborGroupId).add(cell);
        } else {
            // If no existing group was found, create a new group
            HashSet<HexCube> newGroup = new HashSet<>();
            newGroup.add(cell);
            newGroup.add(neighbor);
            groups.put(nextGroupId++, newGroup);
        }
    }

    public void newGroup(HexCube cell) {  //creates new group
        HashSet<HexCube> newGroup = new HashSet<>();
        newGroup.add(cell);
        groups.put(nextGroupId++, newGroup);
    }
    /**
     * merges two groups of hexes together
     *
     * @param player1 The player who will own the merged group
     * @param cell1   A hex from player 1
     * @param player2 player that owns cell to be merged
     * @param cell2   hex that was adjacent to the cell1 that will be merged
     *
     * @implNote If player1 and player2 are different, all hexes from player2's
     *          group will be transferred to player1's ownership. If they're the
     *          same player this combines the two groups from the same player.
     */
    public static void mergePlayerGroups(Player player1, HexCube cell1, Player player2, HexCube cell2) {
        Integer group1Id = player1.getGroupId(cell1);
        Integer group2Id = player2.getGroupId(cell2);

        if (group1Id != null && group2Id != null) {
            // find both groups
            HashSet<HexCube> group1 = player1.groups.get(group1Id);
            HashSet<HexCube> group2 = player2.groups.get(group2Id);
            for(HexCube cube : group2) {
                cube.setOccupant(player1);
            }
            // create a new group
            HashSet<HexCube> mergedGroup = new HashSet<>();
            mergedGroup.addAll(group1);
            mergedGroup.addAll(group2);

            // Remove old groups from both players
            player1.groups.remove(group1Id);
            player2.groups.remove(group2Id);

            // Add the merged group to player1
            player1.groups.put(player1.nextGroupId++, mergedGroup);
        }
    }


    public String getName() {
        return name;
    }

    public String getColour() {
        return colour;
    }

    public int getNumCells() {
        return numCells;
    }

    public void addCell() {
        this.numCells++;
    }
    public void addCell(int n ){
        this.numCells += n;
    }

}