/*
 * The MIT License
 *
 * Copyright 2015 Mark A. Heckler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.thehecklers.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.logging.Level;
import java.util.logging.Logger;


/** Reading is a bean to store the values coming from the microcontroller
 * 
 * @author Mark Heckler
 */
public class Reading {
    public static final int HUMIDITY = 0, 
            TEMPERATURE = 1,
            PRESSURE = 2;

    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    private Integer id;
    private double hum, temp;
    private long pressure;

    /*
        MH add:
        Radioactivity levels (beta and gamma)
        Bearing/directionality on three planes (x, y, z)
    */

    public Reading() {
        //this.id = -1;
        this.hum = -1d;
        this.temp = -1d;
        this.pressure = -1l;
    }

    public Reading(Integer id, double hum, double temp, long pressure) {
        this.id = id;
        this.hum = hum;
        this.temp = temp;
        this.pressure = pressure;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getHum() {
        return hum;
    }

    public void setHum(double hum) {
        this.hum = hum;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public long getPressure() {
        return pressure;
    }

    public void setPressure(long pressure) {
        this.pressure = pressure;
    }
    
    public String toJson() {
        String json = "";
        
        try {
            json = objectWriter.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Reading.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return json;
    }
    
    @Override
    public String toString() {
        return "Id=" + id + ", hum=" + hum + ", temp=" + temp + ", pressure=" + pressure + ".";
    }
}
