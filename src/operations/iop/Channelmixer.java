package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Channelmixer extends DTOperation {

	/**
	 * channelmixer
	 */
	private static final long serialVersionUID = 3139986542759292214L;

	public Channelmixer() {
		super("channelmixer", true);
	}

	@Override
	public void addParam() {
		int CHANNEL_SIZE = 7;
		this.put("red", new DTParameter("float", CHANNEL_SIZE, null));
		this.put("green", new DTParameter("float", CHANNEL_SIZE, null));
		this.put("blue", new DTParameter("float", CHANNEL_SIZE, null));
	}
}
// typedef struct dt_iop_channelmixer_params_t
// {
// //_channelmixer_output_t output_channel;
// /** amount of red to mix value -1.0 - 1.0 */
// float red[CHANNEL_SIZE];
// /** amount of green to mix value -1.0 - 1.0 */
// float green[CHANNEL_SIZE];
// /** amount of blue to mix value -1.0 - 1.0 */
// float blue[CHANNEL_SIZE];
// }
// dt_iop_channelmixer_params_t;

// typedef enum _channelmixer_output_t
// {
// /** mixes into hue channel */
// CHANNEL_HUE=0,
// /** mixes into lightness channel */
// CHANNEL_SATURATION,
// /** mixes into lightness channel */
// CHANNEL_LIGHTNESS,
// /** mixes into red channel of image */
// CHANNEL_RED,
// /** mixes into green channel of image */
// CHANNEL_GREEN,
// /** mixes into blue channel of image */
// CHANNEL_BLUE,
// /** mixes into gray channel of image = monochrome*/
// CHANNEL_GRAY,
//
// CHANNEL_SIZE
// } _channelmixer_output_t;