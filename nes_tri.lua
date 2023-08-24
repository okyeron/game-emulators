-- NES2 Triangle engine test
--
 
 -- Triangle
 -- start     	Linear counter start 0/1
 -- counter		Linear counter 0-127
 -- freq		Frequency (0-2047).
 -- length	 	Length counter (0-31).

engine.name = "NES2_Tri"

local freq = 300
local length = 10
local lincounter = 20

function init()
	engine.amp(0.8)

	engine.attack(0.001)
	engine.release(0.01)
	engine.decay(1)
	engine.sustain(0)

	engine.counter(lincounter) -- 1-127
	engine.start(0)

	engine.freq(freq)
	engine.length(length)

	redraw()
end

function key(n,z)
	if n == 2 and z == 1 then
--	    engine.start(0)
    	engine.counter(100) -- 1-127
		engine.gate(1)
	
	elseif n == 2 and z==0 then
		engine.gate(0)
		
	elseif n == 3 and z==1 then
	elseif n == 3 and z==0 then

	end
	redraw()
end

function enc(n,d)
	if n == 1 then
		lincounter = util.clamp (lincounter + d, 0, 127)  -- (0-2047).
		engine.counter(lincounter)
    
	elseif n == 2 then
		freq = util.clamp (freq + d, 0, 2047)  -- (0-2047).
		engine.freq(freq)

	elseif n == 3 then
		length = util.clamp (length + d, 0, 31) 
		engine.length(length)

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
	screen.text( "NES2_TRI" )
	screen.move(30,8)
	screen.move(10,28)
	screen.text( "freq: " .. freq )
	screen.move(10,38)
	screen.text( "length: " .. length )
	screen.move(10,48)
	screen.text( "counter: " .. lincounter )

	screen.update()
end

function cleanup()
end