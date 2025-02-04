package com.nilsson.sentiment;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class PlotInput {
	int time;
	int value;
}
