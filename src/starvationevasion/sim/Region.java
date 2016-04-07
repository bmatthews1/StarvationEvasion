package starvationevasion.sim;

import starvationevasion.common.Constant;
import starvationevasion.common.EnumFood;
import starvationevasion.common.EnumRegion;
import starvationevasion.common.MapPoint;
import starvationevasion.common.Util;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import java.lang.IllegalStateException;


/**
 * Region class extends AbstractAgriculturalUnit, includes methods for accessing its
 * fields.
 */
public class Region extends Territory
{
  private static final int PLAYER_START_REVENUE = 50; //million dollars
  private static final boolean VERBOSE = false;

  private EnumRegion region;
  private final Area area = new Area();
  private final ArrayList<Territory> territoryList = new ArrayList<>();

  private int revenue;

  public int ethanolProducerTaxCredit = 0;

  private long[][] cropImport = new long[Model.YEARS_OF_DATA][EnumFood.SIZE];  //in metric tons.
  private long[][] cropExport = new long[Model.YEARS_OF_DATA][EnumFood.SIZE];  //in metric tons.
  private long[][] cropConsumption = new long[Model.YEARS_OF_DATA][EnumFood.SIZE]; // in metric tons.
  private long[][] cropProduction = new long[Model.YEARS_OF_DATA][EnumFood.SIZE]; //in metric tons.
  private long[][] cropArea = new long[Model.YEARS_OF_DATA][EnumFood.SIZE]; //square kilometer planted.

  /**
   * Territory constructor
   *
   * @param region
   */
  public Region(EnumRegion region)
  {
    super(region.name());
    this.region = region;
    if (region.isUS())
    {
      revenue = PLAYER_START_REVENUE;
      ethanolProducerTaxCredit = Util.rand.nextInt(15) + Util.rand.nextInt(15);
    }
  }


  public void addProduction(int year, EnumFood food,
                            long imports, long exports, long production,
                            long area)
  {
    int yearIdx = year - Constant.FIRST_DATA_YEAR;
    int cropIdx = food.ordinal();
    cropImport[yearIdx][cropIdx] += imports;
    cropExport[yearIdx][cropIdx] += exports;
    cropConsumption[yearIdx][cropIdx] += production;
    cropProduction[yearIdx][cropIdx] += (production + imports - exports);
    cropArea[yearIdx][cropIdx] += area;
  }

  public long getCropProduction(int year, EnumFood food)
  {
    int yearIdx = year - Constant.FIRST_DATA_YEAR;
    int cropIdx = food.ordinal();
    return cropProduction[yearIdx][cropIdx];
  }

  public long getCropImport(int year, EnumFood food)
  {
    int yearIdx = year - Constant.FIRST_DATA_YEAR;
    int cropIdx = food.ordinal();
    return cropImport[yearIdx][cropIdx];
  }

  public long getCropExport(int year, EnumFood food)
  {
    int yearIdx = year - Constant.FIRST_DATA_YEAR;
    int cropIdx = food.ordinal();
    return cropExport[yearIdx][cropIdx];
  }


  /**
   * Each region is composed of 1 or more territories. This method sets this regions data
   * by aggregating all the region's data. For some data items, such as population,
   * the aggregate is the sum. For other data items the aggregate is weighted average.
   */
  public void aggregateTerritoryData(int year)
  {
    setPopulation(year, 0);
    setUndernourished(year, 0);
    setLandTotal(0);

    for (Territory territory : territoryList)
    {
      int n = getPopulation(year) + territory.getPopulation(year);
      setPopulation(year, n);

      n = getUndernourished(year) + territory.getUndernourished(year);
      setUndernourished(year, n);

      n = getLandTotal() + territory.getLandTotal();
      setLandTotal(n);
    }
  }


  public EnumRegion getRegionEnum()
  {
    return region;
  }

  public int getRevenue()
  {
    return revenue;
  }

  /**
   * @param territory Territory to add to region
   */
  public void addTerritory(Territory territory)
  {
    territoryList.add(territory);
    //area.add(territory.getArea());
  }


  /**
   * Used to link land tiles to a country.
   *
   * @param mapPoint mapPoint that is being testing for inclusing
   * @return true is mapPoint is found inside country.
   */
  public boolean containsMapPoint(MapPoint mapPoint)
  {
    if (territoryList == null)
    {
      throw new RuntimeException("(!)REGIONS NOT SET YET");
    }

    for (Territory t : territoryList)
    {
      if (t.containsMapPoint(mapPoint)) return true;
    }
    return false;
  }


  /**
   * @return regions
   */
  public ArrayList<Territory> getTerritoryList()
  {
    return territoryList;
  }

  /**
   * Estimates the initial yield of all US states the US bookkeeping region.
   */
  public void estimateInitialUSYield()
  {
    /*
    // category divided by the region's people in 1000s of people.
    //
    for (EnumFood crop : EnumFood.values())
    { // Spec section 3.4 2) c) - For each food category find the sum of all 1981
      // state incomes.
      //
      long income = 0;
      for (Territory t : territoryList)
      { if (t.getName().startsWith("US-")) income += t.getCropIncome(crop);
      }

      // The 1981 per capita need for each category of food per 1000 people is
      // the actual domestic consumption of the crop (excluding the underfed
      // people).
      //
      double need = getInitialConsumption(crop, Constant.FIRST_YEAR) / (population[0] - undernourished[0]);

      // Imports & exports per capita for all regions.
      //
      double cropImport = (double) initialImports1981[crop.ordinal()] / population[0];
      double cropExport = (double) initialExports1981[crop.ordinal()] / population[0];

      for (Territory t : territoryList)
      { // Skip the umbrella 'Unitied States' territory object.
	    //
        if (t.getName().startsWith("US-") == false) continue;

        // Spec section 3.4 2) d) - Calculate the production of each category as total
        // US production(Constant.FIRST_YEAR) * state income / total US income.  Note that these values
        // are summed into their respective player region in a subsequent step (section
        // 'e)', sum these totals per region).
        //
        double r = (double) t.getCropIncome(crop) / income;
        t.setCropProduction(crop, (long) (initialProduction1981[crop.ordinal()] * r));

        t.setCropNeedPerCapita(crop, need);
        t.setCropImport(crop, (long) (cropImport * t.getPopulation(Constant.FIRST_YEAR)));
        t.setCropExport(crop, (long) (cropExport * t.getPopulation(Constant.FIRST_YEAR)));
      }
    }

    for (Territory t : territoryList) t.updateYield();
    */
  }

  /**
   * Estimates the initial yield of all territoryList in the region.
   */
  public void estimateInitialYield()
  {
    /*
    if (region == null)
    {
      // At present the only Region that does not correspond to the region enum is
      // the special US bookkeeping region.  This region is special because there
      // are income numbers in the csv file that can be translated to production
      // numbers.
      //
      estimateInitialUSYield();
      return;
    }

    // For United States regions, this data will be populated when the special book-
    // keeping region is visited (above).
    //
    if (region.isUS() == true) return;

    // category divided by the region's people in 1000s of people.
    //
    for (EnumFood crop : EnumFood.values())
    {
      // The 1981 need for each region and category of food per 1000 people is
      // the domestic consumption of the crop.
      //
      double need = getInitialConsumption(crop, Constant.FIRST_YEAR) / (population[0] - undernourished[0]);

      // Production per capita for non-US regions.
      //
      double cropProduction = (double) initialProduction1981[crop.ordinal()] / population[0];

      // Imports & exports per capita for all regions.
      //
      double cropImport = (double) initialImports1981[crop.ordinal()] / population[0];
      double cropExport = (double) initialExports1981[crop.ordinal()] / population[0];

      for (Territory t : territoryList)
      {
        if (t.getName().startsWith("US-") == false)
        {
          t.setCropNeedPerCapita(crop, need);
          t.setCropProduction(crop, (long) (cropProduction * t.getPopulation(Constant.FIRST_YEAR)));
          t.setCropImport(crop, (long) (cropImport * t.getPopulation(Constant.FIRST_YEAR)));
          t.setCropExport(crop, (long) (cropExport * t.getPopulation(Constant.FIRST_YEAR)));
        }
      }
    }

    for (Territory t : territoryList) t.updateYield();
    */
  }

  /**
   * Updates the yield of all territoryList in the region and aggregates the values for
   * the entire region.
   */
  public void updateYield(int year)
  {
    /*
    for (Territory t : territoryList)
    {
      // replant crops based on the new land used for farming
      CropOptimizer cropOptimizer = new CropOptimizer(year, t);
      cropOptimizer.optimizeCrops();

      t.updateYield();
      for (EnumFood crop : EnumFood.values())
      {
        landCrop[crop.ordinal()] += t.landCrop[crop.ordinal()];
        cropProduction[crop.ordinal()] += t.cropProduction[crop.ordinal()];
        cropIncome[crop.ordinal()] += t.cropIncome[crop.ordinal()];
      }
    }

    for (EnumFood crop : EnumFood.values())
    {
      if (landCrop[crop.ordinal()] != 0)
      { cropYield[crop.ordinal()] = cropProduction[crop.ordinal()] / landCrop[crop.ordinal()];
      }
      else cropYield[crop.ordinal()] = 0;
    }
    */
  }

  public void updateCropNeed(int year)
  {

    /*
    double undernourishedRatio = undernourished[year] / getPopulation(year);
    for (EnumFood crop : EnumFood.values())
    {
      int idx = crop.ordinal();
      double consumed = cropProduction[idx] + cropImport[idx] - cropExport[idx];
      setCropNeedPerCapita(crop, consumed, undernourishedRatio);
    }

    */
  }


  /**
   * Estimates the initial crop budget for a all of the territoryList in the region by multiplying the territory
   * consumption of the crop by its cost.
   */
  public void estimateInitialBudget()
  {
    /*
    for (CropZoneData zoneData : cropData)
    {
      double cropConsumptionPerCapita = getInitialConsumption(zoneData.food, Constant.FIRST_YEAR) / getPopulation
      (Constant.FIRST_YEAR);

      for (Territory t : getTerritoryList())
      {
        double territoryCropConsumption = cropConsumptionPerCapita * t.getPopulation(Constant.FIRST_YEAR);
        long budget = (long) territoryCropConsumption * zoneData.pricePerMetricTon;
        t.setCropBudget(zoneData.food, budget);
      }
    }
    */
  }

  public void estimateInitialCropLandArea()
  {

    /*
    setRegionLandTotal();
    for (EnumFood food : EnumFood.values())
    {
      landCrop[food.ordinal()] = 0;
    }

    for (Territory t : getTerritoryList())
    {
      if (t.getGameRegion() != null)
      {
        double territoryFarmLand = (t.farmLand1981 / 100.0) * t.landTotal;
        double helperSum = cropLandAreaHelper(t, cropData);
        double landCropRatio = territoryFarmLand / helperSum;

        for (CropZoneData zoneData : cropData)
        {
          double cropLand = cropLandAreaHelper(t, zoneData) * landCropRatio;
          t.setCropLand(zoneData.food, (int) cropLand);
          landCrop[zoneData.food.ordinal()] += cropLand;
        }
      }
    }
    */
  }

  // defined to be the temp function in the spec
  private double cropLandAreaHelper(Territory t)
  {

    /*
    double productionYieldRatio = initialProduction1981[zoneData.food.ordinal()] / (double) zoneData.tonsPerKM2;
    double landRatio = (double) (t.landTotal * t.totalFarmLand) / (this.landTotal * this.totalFarmLand);
    return productionYieldRatio * landRatio;
    */
    return 0;
  }



  /**
   * Method for calculating and setting crop need
   *
   * @param crop                  EnumFood
   * @param tonsConsumed          2014 production + imports - exports
   * @param percentUndernourished 2014 percent of population undernourished
   */
  public void setCropNeedPerCapita(EnumFood crop, double tonsConsumed, double percentUndernourished)
  {
    /*
    for (Territory unit : territoryList)
    {
      unit.setCropNeedPerCapita(crop, tonsConsumed, percentUndernourished);
    }

    double population = getPopulation(Constant.FIRST_YEAR);
    double tonPerPerson = tonsConsumed / (population - (0.5 * percentUndernourished * population));
    cropNeedPerCapita[crop.ordinal()] = tonPerPerson;
    */
  }

  /**
   * Method for setting crop need when already known (e.g., when copying).
   *
   * @param crop         EnumFood
   * @param tonPerPerson 2014 ton/person
   */
  public void setCropNeedPerCapita(EnumFood crop, double tonPerPerson)
  {
    /*
    // *Changed*
    // amount is already ton per person for the region, so it shouldn't be divided up
    // based on the number of territory.
    // each territory will have the same need per capita as the region.

    // Divide it up amongst the units.
    //
    //double perUnit = tonPerPerson / territoryList.size();
    //double remainder = tonPerPerson % (territoryList.size() * perUnit);
    for (Territory unit : territoryList)
    {
      unit.setCropNeedPerCapita(crop, tonPerPerson);
      //remainder = 0;
    }

    cropNeedPerCapita[crop.ordinal()] = tonPerPerson;
    */
  }

  public MapPoint getCenter()
  {
    return new MapPoint(area.getBounds().getCenterY(), area.getBounds().getCenterX());
  }
}
