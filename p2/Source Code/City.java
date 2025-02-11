import java.awt.*;
import java.util.ArrayList;

/**
 * Represents a city with its name and coordinates.
 */
public class City {
    /** The name of the city. */
    public String cityName;
    /** The x coordinate of the city. */
    public int x0;
    /** The y coordinate of the city. */
    public int y0;
    /** Arraylist of connections to other cities. */
    public ArrayList<City> connections;

    /**
     * Constructs a new city with the given name.
     * @param cityName The name of the current city.
     */
    public City(String cityName) {
        this.cityName = cityName;
        this.connections = new ArrayList<>();
    }

    /**
     * Constructs a new city with the given name and coordinates.
     * @param cityName The name of the current city.
     * @param x0 The x coordinate of the city.
     * @param y0 The y coordinate of the city.
     */
    public City(String cityName, int x0, int y0) {
        this(cityName);
        this.x0 = x0;
        this.y0 = y0;
    }

    /**
     * Draws the city on the map.
     */
    public void drawCity() {
        StdDraw.setFont(new Font("Helvetica", Font.PLAIN, 14));
        StdDraw.point(x0, y0);
        StdDraw.text(x0, y0 + 20, cityName);
    }

    /**
     * Draws a road between this city and its neighboring city.
     * @param city The current city.
     * @param neighborCity The neighboring city.
     */
    public void drawRoad(City city, City neighborCity) {
        StdDraw.line(city.x0, city.y0, neighborCity.x0, neighborCity.y0);
    }

    /**
     * Calculates the distance between this city and its neighboring city.
     * @param city The current city.
     * @param neighborCity The neighboring city.
     * @return The distance between this city and the neighboring city.
     */
    public double distanceCalculator(City city, City neighborCity) {
        return Math.sqrt(Math.pow((city.x0 - neighborCity.x0), 2) +
                Math.pow((city.y0 - neighborCity.y0), 2));
    }
}
