program
	var X as int ;
begin
	X := readint ;
	writeint 2 * X ;

	if X div 2 >= X then
	    X := X - 1 ;
	end
	else
	    X := X + 1 ;
	end
end
