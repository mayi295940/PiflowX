package cn.piflow.conf

import cn.piflow.StreamingStop

abstract class ConfigurableStreamingStop[StreamingContext, DataStream, DStream]
  extends ConfigurableStop[DataStream]
    with StreamingStop[StreamingContext, DataStream, DStream] {

}
