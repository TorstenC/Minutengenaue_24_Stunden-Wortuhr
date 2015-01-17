/**
 * Created by Torsten on 16.01.2015.
 */
package de.tcpix.wordclock24h.app;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class WC24h {
    public class Glyph {
        public View getView;
        public String getChar;
    }
    public class Word {
        public Glyph[] Glyphs;
        public int[] RowColLen;
    }
    private class JsonImport {
        Boolean getJsonOK = true;
        String getJsonParentName;
    }
    public int getWpCount; // todo: implement getter
    public int getColumns; // todo: implement getter
    public int getRows; // todo: implement getter
    public Glyph[] getGlyphs;
    public Word[] getWords;
    protected JsonImport JsonImport;
    public boolean loadJSON(String JSONString) {
        if (this.JsonImport != null) {
            // todo: JsonImport für "return false;" mit "Log.e()" und "Exception e" behalten?
            Log.e("wordclock24h", "WC24h.loadJSON: import already running");
            return false;
        }
        this.JsonImport = new JsonImport();
        JSONObject jsonObj;
        JSONArray jArr;
        JSONArray jArr2;
        String jStr;
        this.JsonImport.getJsonOK = true;
        if (JSONString == null) {
            Log.e("wordclock24h", "WC24h.loadJSON: empty JSON String");
            return false;
        } else {
            try {
                jsonObj = new JSONObject(JSONString);
                Log.i("wordclock24h", jsonObj.toString());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("wordclock24h", "WC24h.loadJSON: invalid JSON String");
                return false;
            }
            Log.i("wordclock24h", jsonObj.toString());
            this.getRows = this.getInt(jsonObj, "WC_ROWS");
            if (!this.JsonImport.getJsonOK) {this.JsonImport = null; return false;}
            this.getColumns = this.getInt(jsonObj,"WC_COLUMNS");
            if (!this.JsonImport.getJsonOK) {this.JsonImport = null; return false;}
            this.getWpCount = this.getInt(jsonObj,"WC_COUNT");
            if (!this.JsonImport.getJsonOK) {this.JsonImport = null; return false;}
            jArr = this.getArray(jsonObj, "display");
            if (!this.JsonImport.getJsonOK) {this.JsonImport = null; return false;}
            if (this.getRows != jArr.length()) {
                Log.e("wordclock24h", "WC24h.loadJSON: display.length() != WC_ROWS");
                return false;
            } else {
                this.getGlyphs = new Glyph[this.getRows * this.getColumns];
                int index = 0;
                for (int row = 0; row < this.getRows; row++){
                    jStr = this.getString(jArr, row);
                    if (!this.JsonImport.getJsonOK) {this.JsonImport = null; return false;}
                    if (jStr.length() != this.getColumns)  {
                        Log.e("wordclock24h", "WC24h.loadJSON: display["+ row +"] != WC_COLUMNS");
                        return false;
                    } else {
                        for (int col = 0; col < this.getColumns; col++) {
                            String ST =  jStr.substring(col, col + 1) + "";
                            this.getGlyphs[index] = new Glyph();
                            this.getGlyphs[index].getChar = ST;
                        }
                    }
                }
            }
            jArr = this.getArray(jsonObj, "illumination");
            if (!this.JsonImport.getJsonOK) {this.JsonImport = null; return false;}
            if (this.getWpCount != jArr.length()) {
                Log.e("wordclock24h", "WC24h.loadJSON: illumination.length() != WC_COUNT");
                return false;
            } else {
                this.getWords = new Word[this.getWpCount];
                for (int i = 0; i < this.getWpCount; i++ ) {
                    jArr2 = this.getArray(jArr, i);
                    if (!this.JsonImport.getJsonOK) {this.JsonImport = null; return false;}
                    if (jArr2.length() != 3) {
                        Log.e("wordclock24h", "WC24h.loadJSON: display[" + i + "].length() != 3");
                        return false;
                    }
                    this.getWords[i] = new Word();
                    this.getWords[i].RowColLen = new int[3];
                    this.getWords[i].RowColLen[0] = this.getInt(jArr2, 0);
                    this.getWords[i].RowColLen[1] = this.getInt(jArr2, 1);
                    this.getWords[i].RowColLen[2] = this.getInt(jArr2, 2);
                }
            }

            jArr = this.getArray(jsonObj, "tbl_modes");
            if (!this.JsonImport.getJsonOK) {this.JsonImport = null; return false;}
            if (this.getWpCount != jArr.length()) {
                Log.e("wordclock24h", "WC24h.loadJSON: illumination.length() != WC_COUNT");
                return false;
            } else {
                this.getWords = new Word[this.getWpCount];
                for (int i = 0; i < this.getWpCount; i++ ) {
                    jArr2 = this.getArray(jArr, i);
                    if (!this.JsonImport.getJsonOK) {this.JsonImport = null; return false;}
                    if (jArr2.length() != 3) {
                        Log.e("wordclock24h", "WC24h.loadJSON: display[" + i + "].length() != 3");
                        return false;
                    }
                }
            }

            this.JsonImport = null;
            return true;
        }
    }
    protected int getInt(JSONObject jsonObj, String Name) {
        try {
            return jsonObj.getInt(Name);
        } catch (JSONException e) {
            this.JsonImport.getJsonOK = false;
            e.printStackTrace();
            Log.e("wordclock24h", "WC24h.loadJSON: invalid JSON Integer: " + Name);
            return 0;
        }
    }
    protected int getInt(JSONArray jsonArr, int index) {
        try {
            return jsonArr.getInt(index);
        } catch (JSONException e) {
            this.JsonImport.getJsonOK = false;
            e.printStackTrace();
            Log.e("wordclock24h", "WC24h.loadJSON: invalid JSON Array Index: " + this.JsonImport.getJsonParentName + "[" + index+ "]");
            return 0;
        }
    }
    protected JSONArray getArray(JSONArray jsonArr, int index) {
        try {
            return jsonArr.getJSONArray(index);
        } catch (JSONException e) {
            this.JsonImport.getJsonOK = false;
            e.printStackTrace();
            Log.e("wordclock24h", "WC24h.loadJSON: invalid JSON Array Index: " + this.JsonImport.getJsonParentName + "[" + index+ "]");
            return null;
        }
    }
    protected String getString(JSONArray jsonArr, int index) {
        try {
            return jsonArr.getString(index);
        } catch (JSONException e) {
            this.JsonImport.getJsonOK = false;
            e.printStackTrace();
            Log.e("wordclock24h", "WC24h.loadJSON: invalid JSON String Index: " + this.JsonImport.getJsonParentName + "[" + index+ "]");
            return "";
        }
    }
    protected JSONArray getArray(JSONObject jsonObj, String Name) {
        try {
            this.JsonImport.getJsonParentName=Name;
            return jsonObj.getJSONArray(Name);
        } catch (JSONException e) {
            this.JsonImport.getJsonOK = false;
            e.printStackTrace();
            Log.e("wordclock24h", "WC24h.loadJSON: invalid JSON Array: " + Name);
            return null;
        }
    }
}
