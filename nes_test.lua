-- NES2 engine test
--
-- NES Square only implemented so far

-- trig		Control or audio rate trigger.
-- dutycycle	Type (0-3).
-- loopenv		Loop envelope off or on (0/1).
-- envdecay	Envelope decay off or on (0/1).
-- vol			Volume (0-15).
-- sweep		Off or on (0/1).
-- sweeplen	Sweeplength (0-7).
-- sweepdir	Sweepdirection decrease or increase (0/1).
-- sweepshi	Sweepshift (0-7).
-- freq		Frequency (0-2047).
-- vbl	 		Length counter (0-31).


engine.name = "NES"

local freq = math.random(1,1023)
local vbl = 10

function init()
  engine.dutycycle(0)
  engine.loopenv(0)
  engine.envdecay(0)
  engine.vol(10)
  engine.sweep(1)
  engine.sweeplen(7)
  engine.sweepdir(0)
  engine.sweepshi(7)
  engine.freq(freq)
  engine.vbl(vbl)
  
  counter = metro.init()
  counter.time = 1 -- interval
  counter.event = trigger
  --counter:start()
  --  redraw()
end

function trigger()
  --freq = freq + 10
  --engine.vbl(vbl)
  --engine.noteOn (freq)
  --engine.noteOff()
  print(freq)
  redraw()
end

function key(n,z)
  if n == 2 and z == 1 then
    --counter:start()
    --freq = math.random(1,1023)
    engine.vbl(vbl)
    engine.noteOn (freq)
    
  elseif n == 2 and z==0 then
    engine.noteOff ()

  elseif n == 3 and z==1 then
    --counter:stop()
  elseif n == 3 and z==0 then
    --position = 0
  end
  redraw()
end

function enc(n,d)
  if n == 2 then
    freq = freq + d
    
  elseif n == 3 then
    vbl = vbl + d
  
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
  screen.text( "NES2" )
  screen.move(30,8)
  screen.move(30,18)
  screen.text( "freq: " .. freq )
  screen.move(30,28)
  screen.text( "vbl: " .. vbl )

  screen.update()
end

