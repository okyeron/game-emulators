-- NES2 engine test
--
-- NES Square

engine.name = "NES2"

local freqsq = 600 --math.random(1,1023)
local freqtri = 300
local vblsq = 6
local vbltri = 6

function init()
  engine.dutycycle(0)
  engine.loopenv(0)
  engine.envdecay(1)
  engine.vol(8)
  engine.sweep(0)
  engine.sweeplen(7)
  engine.sweepdir(1)
  engine.sweepshi(7)
  engine.freqsq(freqsq)
  engine.vblsq(vblsq)
  engine.onOff(0)


  engine.start(0) -- 0/1
  engine.counter(20) -- 1-127
  engine.freqtri(freqtri)
  engine.vbltri(vbltri)

  counter = metro.init()
  counter.time = 1/8 -- interval
  counter.event = trigger
  --counter:start()
  --redraw()
end

function trigger()
    --engine.vblsq(vblsq)
    --freqsq = math.random(100, 2047)
    --engine.freqsq (freqsq)
    --engine.bangSq()
  redraw()
end

function key(n,z)
  if n == 2 and z == 1 then
    --counter:start()
    --freqsq = math.random(1,1023)
    engine.vblsq(vblsq)
    engine.freqsq (freqsq)
    engine.bangSq()

    engine.vbltri(vbltri)
    engine.freqtri (freqtri)
    engine.bangTri()
    
  elseif n == 2 and z==0 then
    --engine.noteOff ()

  elseif n == 3 and z==1 then
    counter:stop()
  elseif n == 3 and z==0 then

  end
  redraw()
end

function enc(n,d)
  if n == 2 then
    freqsq = freqsq + d
    
  elseif n == 3 then
    vblsq = vblsq + d
  
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
  screen.text( "freqsq: " .. freqsq )
  screen.move(30,28)
  screen.text( "vblsq: " .. vblsq )

  screen.update()
end

