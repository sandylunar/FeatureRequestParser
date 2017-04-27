package main.java.predictor;

import main.java.bean.FeatureRequestOL;
import weka.core.Instance;
import weka.core.Instances;

public interface TagPredictor {

	public int predictTagIndex(Instance item, Instances data, FeatureRequestOL request, int index);
	
}
