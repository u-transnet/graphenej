package com.github.utransnet.graphenej;

import com.google.common.primitives.UnsignedLong;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Artem on 22.03.2018.
 */
public class FeeForProposing {
    public AssetAmount proposingFee;
    public List<AssetAmount> proposedOpsFee;

    public FeeForProposing(AssetAmount proposingFee, List<AssetAmount> proposedOpsFee) {
        this.proposingFee = proposingFee;
        this.proposedOpsFee = proposedOpsFee;
    }

    /**
     * Custom deserializer used for this class
     */
    public static class FeeForProposingDeserializer implements JsonDeserializer<FeeForProposing> {

        @Override
        public FeeForProposing deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray jsonArray = json.getAsJsonArray();
            JsonElement proposingFee = jsonArray.get(0);
            AssetAmount assetAmount = AssetAmount.fromJsonObject(proposingFee);

            ArrayList<AssetAmount> assetAmounts = new ArrayList<>();
            if(jsonArray.get(1).isJsonArray()) {
                for (JsonElement fee : jsonArray.get(1).getAsJsonArray()) {
                    assetAmounts.add(AssetAmount.fromJsonObject(fee));
                }

            }

            return new FeeForProposing(assetAmount, assetAmounts);
        }
    }
}
