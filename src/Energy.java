//package grid_tools;

import java.io.Serializable;

/**
*	Energy objects hold energy states for the Wang-Landau algorithm.
*	This class is designed to be updateable for new energy types.
*/
class Energy implements Serializable {
    /**
    *	serialVersionUID used for serializing Energy objects,
    *	which is needed when saving a HashMap of Wang-Landau Weights
    */
    public static final long serialVersionUID = 0;

    /*	Constants for specifying energy type.
    *	To add a new energy type, add a constant here, then add it to
    *	the switch statements in the constructors and the hashCode method,
    *	then change the serialVersionUID
    */
    public static final int ENERGYTYPE_SIZE = 1;
    public static final int ENERGYTYPE_WRITHE = 2;

    //Stores the actual energy type. Note that this is a static variable.
    private static int[] energyType;

    //The actual energy values for the current energy object.
    private final Serializable[] energyStates;

    //Helper variables in case the object gets hashed more than once
    private int cachedHash;
    private boolean isHashCached;

    /**
    *	Construct the Energy object from a grid diagram
    * 	@param gDiagram the grid diagram to calculate the energy state of
    */
    public Energy(GridDiagram gDiagram){
        energyStates = new Serializable[energyType.length];
        for (int i = 0; i < energyType.length; i++) {
            switch (energyType[i]) {
                case ENERGYTYPE_SIZE:
                    energyStates[i] = gDiagram.getSize();
                    break;
                case ENERGYTYPE_WRITHE:
                    energyStates[i] = gDiagram.calcWrithe();
                    break;
            }
        }
        isHashCached = false;
    }
    /**
    *	Construct the Energy object for the grid diagram resulting from a Cromwell move
    * 	@param currentEnergy The energy state of the current grid diagram
    *	@param gDiagram The current grid diagram which is being transformed
    *	@param movetype The move being performed. Use the GridDiagram MOVETYPE constants for this argument
    *	@param arguments The arguments that would be fed into the move's method to perform the move
    */
    public Energy(Energy currentEnergy, GridDiagram gDiagram, int movetype, int[] arguments){
        Serializable[] currentEnergyArr = currentEnergy.getEnergyState();
        energyStates = new Serializable[currentEnergyArr.length];
        for (int i=0; i<energyType.length; i++){
            switch (energyType[i]){
                case ENERGYTYPE_SIZE:
                    energyStates[i] = (int)currentEnergyArr[i] + GridDiagram.deltaSize(movetype);
                    break;
                case ENERGYTYPE_WRITHE:
                    energyStates[i] = (int)currentEnergyArr[i] + gDiagram.deltaWrithe(movetype, arguments);
                    break;
            }
        }
        isHashCached = false;
    }

    /**
    * Construct the Energy object from an array of energy values representing the current energy state
    * @param currentState an array of energy values where the value in index i corresponds to energyType[i]
    */
    public Energy(Serializable[] currentState){
        energyStates = currentState;
    }

    /**
    *	@return an array of the current energy values with index i corresponding to energyType[i]
    */
    public Serializable[] getEnergyState(){
        return energyStates;
    }

    /**
    *	Note that this method is static, so all Energy objects should use the same energyType
    *	@param newEnergyType an array of ENERGYTYPE constants (from this class)
    */
    public static void setEnergyType(int[] newEnergyType){
        energyType = newEnergyType;
    }


    /**
    *	Two Energy objects are equal if each of their energy values are equal
    */
    @Override
    public boolean equals(Object o){
        if (o instanceof Energy) {
            Serializable[] otherEnergyArr = ((Energy) o).getEnergyState();
            if (otherEnergyArr.length != energyStates.length) {
                return false;
            }
            for (int i = 0; i < energyStates.length; i++) {
                if (!otherEnergyArr[i].equals(energyStates[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(){
        if (!isHashCached){
            int calcHash = 0;
            for (int i=0; i<energyStates.length; i++){
                switch (energyType[i]){
                    case ENERGYTYPE_SIZE:
                        calcHash = (cachedHash*31)+(int)energyStates[i];
                        break;
                    case ENERGYTYPE_WRITHE:
                        calcHash = (cachedHash*31)+(int)energyStates[i];
                        break;
                }
            }
            cachedHash = calcHash;
            isHashCached = true;
        }
        return cachedHash;
    }

    /**
    *	Prints the energy state as a tuple
    */
    @Override
    public String toString() {
        String thisString = "(";
        for (int i = 0; i < energyStates.length; i++) {
            thisString += energyStates[i].toString();
            if (i < energyStates.length - 1) {
                thisString += ", ";
            }
        }
        return thisString+")";
    }
}