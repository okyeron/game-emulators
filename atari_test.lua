-- atari 2600 engine test
--
-- KEY 2 toggle sound on/off
-- KEY 3 trigger a sound
-- ENC 2 choose tone
-- ENC 3 change pitch
--

-- freq0, freq1 are 5 bit pitch (32 values)
-- vol0, vol1 are 4 bit volume control (16 values)
-- tone0, tone1 are as follows:

-- Tone NAME    DESCRIPTION
--  0   nothing
--  1   Saw     sounds similar to a saw waveform
--  2			      low pitch
--  3   Engine  many 2600 games use this for an engine sound
-- 4/5  Square  a high pitched square waveform
--  6   Bass    fat bass sound
--  7   Pitfall log sound in pitfall, low and buzzy
--  8   Noise   white noise
--  9 			     similar to 7
-- 10   sine?
-- 11   nothing
-- 12/13   Lead    lower pitch square wave sound
-- 14   Bass    Another bassy sound
-- 15   Buzz    atonal buzz, good for percussion


engine.name = "Atari2600"

function init()
  engine.attack(0.01)
  engine.sustain(0.2)
  engine.release(0.05)
  engine.freq0(24)
  engine.tone0(6)
  engine.freq1(20)
  engine.tone1(8)

  tone = 8
  sound = 1
  level = .2
  position = 0
  freq = math.random(1,32)

  counter = metro.init()
  counter.time = 1/64 -- interval
  counter.event = pewpew
  

end

function pewpew()
      engine.attack(0.04)
      engine.sustain(0.03)
      engine.release(0.05)

      engine.tone0(1)
      engine.freq0(5 + position)
      engine.tone1(12)
      engine.freq1(3 + position)
      engine.trig()
      if position >32 then
        position = 1
      else
        position = position + 1
      end
end

function key(n,z)
  if n == 2 and z == 1 then
      -- trick below to toggle between 0 and 1
--      sound = 1 - sound
--      engine.amp(sound * level)
    --tone = math.random(1,15)
    engine.tone0(tone)
    engine.freq0(freq)
    engine.tone1(tone)
    engine.freq1(freq)
    engine.trig()

  elseif n == 3 and z==1 then
    --tone = math.random(1,15)
    --engine.tone0(tone)
    --engine.freq0(freq)
    --engine.tone1(tone)
    --engine.freq1(freq)
    --engine.trig()
    counter:start()
  elseif n == 3 and z==0 then
    counter:stop()
    position = 0
  end
end

function enc(n,d)
  if n == 2 then
    tone = tone + d
  elseif n == 3 then
    freq = freq + d
  end
  redraw()
end

function redraw()
  screen.clear()
  screen.level(15)
  screen.line_width(1)
  screen.font_face(0)
  screen.font_size(8)
  screen.move(1,8)
  screen.text( "2600" )
  screen.move(30,8)
  screen.text( "tone0: " .. tone )
  screen.move(30,18)
  screen.text( "freq0: " .. freq )
  screen.move(30,28)
  screen.text( "tone1: " .. tone )
  screen.move(30,38)
  screen.text( "freq1: " .. freq )
  screen.update()
end

